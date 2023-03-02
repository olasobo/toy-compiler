import java.io.IOException;

public class dasm {

    private static int getValue(String s) {
        int radix = 10;
        if (s.startsWith("0x") || s.startsWith("0X")) {
            s = s.substring(2);
            radix = 16;
        } else if (s.startsWith("0b") || s.startsWith("0B")) {
            s = s.substring(2);
            radix = 2;
        } else if (s.startsWith("0")){
            radix = 8;
        } else {
            radix = 10;
        }

        int result =  Integer.parseInt(s, radix);
        if (result < 0) throw new IllegalArgumentException();
        return result;
    }

    private static int wrap(int value) {
        return value & 0xFFFF;
    }

    private static int roundUpToNextPowerOfTwo(int value) {
        int result = 1;
        while (result < value) {
            result *= 2;
        }
        return result;
    }

    private static int roundUpToNextMultiple(int value, int multiple) {
        return (value + multiple - 1) / multiple * multiple;
    }

    private static int roundDownToNextMultiple(int value, int multiple) {
        return roundUpToNextMultiple(value - multiple + 1, multiple);
    }

    private static boolean isOption(String arg) {
        if (arg.length() > 0) {
            return arg.startsWith("-") || arg.startsWith("+");
        } else {
            return false;
        }
    }


    private static void readFile(String filename) throws IOException {
        if (filename.equals("-")) {
            Memory.load();
        } else {
            Memory.load(filename);
        }
    }

    private static void readFile() {
        Memory.load();
    }


    private static void disassemble(
        int     start,
        int     count,
        boolean addresses,
        boolean halt
    ) {
        int address = start;
        while (address < Memory.SIZE && count-- > 0) {
            Instruction instruction = Instruction.valueOf(Memory.readWord(address));
            Opcode opcode = instruction.opcode();
            Disassembler.disassemble(System.out, address, addresses, addresses, true);
            if (halt && opcode == Opcode.HALT) break;
            address += opcode.size();
        }
    }


    public static void main(String[] args) {
        String option = "";
        boolean dumpAddress = true;
        boolean halt = true;
        int width = 16;
        int start = 0;
        int stop = Memory.SIZE-1;
        int count = Memory.SIZE/2;
        int files = 0;
        int errors = 0;

        for (String arg : args) {
            if (isOption(arg) && isOption(option)) {
                System.err.println("Missing value for option " + option);
                option = "";
                errors++;
            }

            switch (arg.toLowerCase()) {
                case "-width":
                case "-count":
                case "-begin":
                case "-start":
                    option = arg;
                    continue;

                case "-addr":
                case "-address":
                case "-addresses":
                    dumpAddress = true;
                    continue;

                case "+addr":
                case "+address":
                case "+addresses":
                    dumpAddress = false;
                    continue;

                case "-halt":
                    halt = true;
                    continue;

                case "+halt":
                    halt = false;
                    continue;

                case "-stdin":
                    readFile();
                    continue;

                case "-help":
                    help();
                    return;

                case "":
                    continue;

                default:
                    if (isOption(arg)) {
                        System.err.println("Invalid option " + arg);
                        errors++;
                        continue;
                    }
            }

            try {
                switch (option.toLowerCase()) {
                    case "-width":
                        width = getValue(arg);
                        break;

                    case "-count":
                        count = getValue(arg);
                        break;

                    case "-begin":
                    case "-start":
                        start = getValue(arg);
                        break;

                    default:
                        readFile(arg);
                        files++;
                        break;
                }

            } catch (NumberFormatException e) {
                System.err.println("Invalid valid for option " + option + ": " + arg);
                errors++;
            } catch (IOException e) {
                System.err.println("Error reading input file: " + e.getMessage());
                errors++;
            }

            option = "";
        }

        if (errors > 0) return;

        disassemble(start, count, dumpAddress, halt);
    }

    
    public static void help() {
        System.out.println();
        System.out.println("  java dasm <options> <files>");
        System.out.println();
        System.out.println("    Diassemble a TINY object code file:");
        System.out.println();
        System.out.println("    -width #            The number of bytes to display on each line");
        System.out.println("    -count #            The maximum number of instructions to display");
        System.out.println("    -begin #            The address at which to begin disassembling code");
        System.out.println("    -address            Enable displaying the address of each instruction");
        System.out.println("    +address            Disable displaying the address of each instruction");
        System.out.println("    -halt               Stop diassembling upon a HALT instruction");
        System.out.println("    +halt               Continue disassembly after HALT instructions");
        System.out.println("    -stdin              Read object code file from standard input");
        System.out.println("    <files>             One or more TINY object code files to be disassembled");
        System.out.println();
    }
}
