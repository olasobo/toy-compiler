import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

public class Assembler {

    public static boolean trace = false;
    public static int bytesGenerated = 0;
    public static int instructionsGenerated = 0;

    private static void trace(int address, int value) {
        if (Assembler.trace) {
            System.out.println(String.format("%04x: %02x", address, value).toUpperCase());
        }
    }

    private static void trace(String line) {
        if (Assembler.trace) {
            System.out.println(line);
        }
    }


    // -- Exceptions ------------------------------------------------------------------

    public static class AsmException extends RuntimeException {
        public AsmException(String message) { super(message); }
    }

    public static class Defined extends AsmException {
        public Defined(String name) {
            super("Multiply defined: " + name);
        }
    }

    public static class Undefined extends AsmException {
        public Undefined(String name) {
            super("Undefined: " + name);
        }
    }

    public static class TooMany extends AsmException {
        public TooMany(String[] operands, int max) {
            super("Too many operands: " + operands.length + ", expecting " + max);
        }
    }

    public static class TooFew extends AsmException {
        public TooFew(String[] operands, int min) {
            super("Too few operands: " + operands.length + ", expecting " + min);
        }
    }

    public static class BadOpcode extends AsmException {
        public BadOpcode(String opcode) {
            super("Invalid opcode: " + opcode);
        }
    }

    public static class BadOperand extends AsmException {
        public BadOperand(int value) {
            super("Invalid operand value: " + value);
        }
    }

    public static class BadSyntax extends AsmException {
        public BadSyntax() {
            super("Syntax error");
        }

        public BadSyntax(String details) {
            super("Syntax error: " + details);
        }
    }


    // -- Utilities -------------------------------------------------------------------

    private static Opcode getOpcode(String mnemonic) {
        try {
            return Opcode.valueOf(mnemonic.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean isDefined(String name) {
        if (name != null && name.length() > 0) {
            return Symbols.isDefined(name);
        } else {
            return false;
        }
    }

    private static void define(String name, int value) {
        if (name != null && name.length() > 0) {
            Symbols.define(name, value);
        }
    }

    private static int getValue(String operand) {
        return Parser.parse(operand);
    }

    private static int lo(int value) {
        return value & 0xFF;
    }

    private static int hi(int value) {
        return (value >> 8) & 0xFF;
    }

    private static String getStringValue(String operand) {
        boolean escaped = false;
        String result = "";
        for (int i = 0; i < operand.length(); i++) {
            char c = operand.charAt(i);
            if (escaped) {
                switch (c) {
                    case '\'': result += c; break;
                    case '\"': result += c; break;
                    case '\\': result += c; break;
                    case 't':  result += '\t'; break;
                    case 'n':  result += '\n'; break;
                    case 'r':  result += '\r'; break;
                    case '0':  result += '\000'; break;
                    default:   // Illegal escape
                }
                escaped = false;
            } else {
                switch (c) {
                    case '\"': break;
                    case '\\': escaped = true; break;
                    default:  result += c;
                }
            }
        }
        return result;
    }

    // -- Emit ------------------------------------------------------------------------

    private static int dot = 0;  // Current emit location
    private static int here = 0; // Dot at start of line

    public static int dot() {
        return Assembler.here;
    }

    public static void dot(int address) {
        Assembler.here = address;
    }

    private static void emitByte(int value) {
        Assembler.bytesGenerated++;
        trace(Assembler.dot, value);
        Memory.writeByte(Assembler.dot, value);
        Assembler.dot += 1;
    }

    private static void emitWord(int value) {
        Assembler.bytesGenerated += 2;
        trace(Assembler.dot, value & 0xFF);
        trace(Assembler.dot+1, (value >> 8) & 0xFF);
        Memory.writeWord(Assembler.dot, value);
        Assembler.dot += 2;
    }

    private static void processOrg(String label, String[] operands) {
        if (isDefined(label)) throw new Defined(label);
        if (operands.length > 1) throw new TooMany(operands, 1);
        if (operands.length < 1) throw new TooFew(operands, 1);
        Assembler.dot = getValue(operands[0]);
        define(label, Assembler.dot);
    }

    private static void processSet(String label, String[] operands) {
        if (isDefined(label)) throw new Defined(label);
        if (operands.length > 1) throw new TooMany(operands, 1);
        if (operands.length < 1) throw new TooFew(operands, 1);
        define(label, getValue(operands[0]));
    }

    private static void processSpace(String label, String[] operands) {
        if (isDefined(label)) throw new Defined(label);
        if (operands.length > 1) throw new TooMany(operands, 1);
        if (operands.length < 1) throw new TooFew(operands, 1);
        define(label, Assembler.dot);
        Assembler.dot += getValue(operands[0]);
    }

    private static void processByte(String label, String[] operands) {
        if (isDefined(label)) throw new Defined(label);
        if (operands.length < 1) throw new TooFew(operands, 1);
        define(label, Assembler.dot);
        for (String operand : operands) {
            emitByte(getValue(operand));
        }
    }

    private static void processWord(String label, String[] operands) {
        if (isDefined(label)) throw new Defined(label);
        if (operands.length < 1) throw new TooFew(operands, 1);
        define(label, Assembler.dot);
        for (String operand : operands) {
            try {
                int value = getValue(operand);
                emitWord(value);
            } catch (Undefined e) {
                Symbols.patch(Assembler.dot, operand);
                emitWord(Assembler.here); // Save value of dot for fixups
            }
        }
    }

    private static void processASCII(String label, String[] operands) {
        if (isDefined(label)) throw new Defined(label);
        if (operands.length < 1) throw new TooFew(operands, 1);
        define(label, Assembler.dot);
        for (String operand : operands) {
            String value = getStringValue(operand);
            for (int i = 0; i < value.length(); i++) {
                emitByte(value.charAt(i));
            }
        }
        emitByte(0); // Null terminate
    }

    private static void processCode(String label, String mnemonic, String[] operands) {
        Assembler.instructionsGenerated++;
        Opcode opcode = getOpcode(mnemonic.toUpperCase());
        Instruction instruction = Instruction.valueOf(opcode);
        if (opcode == null) throw new BadOpcode(mnemonic);
        int numberOperands = instruction.operands();

        if (isDefined(label)) throw new Defined(label);
        if (operands.length > numberOperands) throw new TooMany(operands, numberOperands);
        if (operands.length < numberOperands) throw new TooFew(operands, numberOperands);

        define(label, Assembler.dot);
        int encoding = opcode.opcode();
        for (int i = 0; i < numberOperands; i++) {
            Operand operand = instruction.getOperand(i);
            if (!operand.additionalWord()) {
                int value = getValue(operands[i]);
                if (!operand.isValid(value)) throw new BadOperand(value);
                encoding |= operand.encode(value);
            }
        }
        emitWord(encoding);

        for (int i = 0; i < numberOperands; i++) {
            Operand operand = instruction.getOperand(i);
            if (operand.additionalWord()) {
                try {
                    int value = getValue(operands[i]);
                    emitWord(value);
                } catch (Undefined e) {
                    Symbols.patch(Assembler.dot, operands[i]);
                    emitWord(Assembler.here); // Save value of dot for fixups
                }
            }
        }
    }


    // -- Parsing ---------------------------------------------------------------------

    private static boolean isWhite(char c) {
        return Character.isWhitespace(c);
    }

    private static int skipWhite(String line, int start) {
        while (start < line.length() && isWhite(line.charAt(start))) {
            start++;
        }
        return start;
    }

    private static int skipToWhite(String line, int start) {
        while (start < line.length() && !isWhite(line.charAt(start))) {
            start++;
        }
        return start;
    }

    private static String stripComments(String line) {
        int index = line.indexOf("//");
        if (index >= 0) {
            return line.substring(0, index);
        } else {
            return line;
        }
    }

    private static String substring(String line, int start, int end) {
        if (end > start) {
            return line.substring(start, end);
        } else {
            return "";
        }
    }

    private static String getLabel(String field) {
        if (field != null && field.length() > 0) {
            return field.substring(0, field.length()-1);
        } else {
            return "";
        }
    }

    private static String[] getOperands(String field) {
        if (field != null && field.length() > 0) {
            return field.split(",");
        } else {
            return new String[] {};
        }
    }

    private static void processLine(String line) {
        trace("");
        trace(line);
        line = stripComments(line);

        int labelStart = 0;
        int labelEnd = skipToWhite(line, labelStart);
        String labelField = substring(line, labelStart, labelEnd);
        String label = getLabel(labelField);

        int opcodeStart = skipWhite(line, labelEnd);
        int opcodeEnd = skipToWhite(line, opcodeStart);
        String opcodeField = substring(line, opcodeStart, opcodeEnd);
        String opcode = opcodeField;

        int operandStart = skipWhite(line, opcodeEnd);
        int operandEnd = line.length();
        String operandField = substring(line, operandStart, operandEnd);
        String[] operands = getOperands(operandField);

        Assembler.here = Assembler.dot;
        switch (opcode.toUpperCase()) {
            case "":
                define(label, Assembler.dot);
                break;

            case ".SET":
                processSet(label, operands);
                break;

            case ".ORG":
                processOrg(label, operands);
                break;

            case ".BYTE":
                processByte(label, operands);
                break;

            case ".WORD":
                processWord(label, operands);
                break;

            case ".SPACE":
                processSpace(label, operands);
                break;

            case ".ASCII":
                processASCII(label, operands);
                break;

            default:
                processCode(label, opcode, operands);
                break;
        }
    }


    // -- File I/O --------------------------------------------------------------------

    public static int processFile(Scanner input) {
        int lineNumber = 0;
        int errors = 0;
        while (input.hasNextLine()) {
            String line = input.nextLine();
            lineNumber++;
            try {
                processLine(line);
            } catch (AsmException e) {
                System.err.println("Line " + lineNumber + ": " + e.getMessage());
                errors++;
            }
        }
        return errors;
    }

    public static int processFile(InputStream input) {
        return processFile(new Scanner(input));
    }

    public static int processFile() {
        return processFile(System.in);
    }

    public static int processFile(String filename) throws IOException {
        Scanner input = new Scanner(new File(filename));
        return processFile(input);
    }

    public static void initialize() {
        Symbols.initialize();
        Memory.clear();
    }

    public static int finish() {
        return Symbols.resolve();
    }
}
