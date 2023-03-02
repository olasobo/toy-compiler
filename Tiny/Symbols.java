import java.util.HashMap;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

public class Symbols {

    private static final HashMap<String, Integer> symbols = new HashMap<>();
    private static final HashMap<Integer, String> patches = new HashMap<>();

    private static void addRegister(String name, int value) {
        define(name.toLowerCase(), value);
        define(name.toUpperCase(), value);
    }

    public static void initialize() {
        for (Register register : Register.registers) {
            addRegister(register.name(), register.number());
        }
        addRegister("SP", Register.SP.number());
    }

    public static boolean isDefined(String name) {
        return symbols.containsKey(name);
    }

    public static void define(String name, int value) {
        symbols.put(name, value);
    }

    public static int get(String name) {
        return symbols.get(name);
    }

    public static void patch(int address, String name) {
        patches.put(address, name);
    }

    public static int resolve() {
        int unresolved = 0;
        for (Integer address : patches.keySet()) {
            String operand = patches.get(address);
            Assembler.dot(Memory.readWord(address));
            try {
                int value = Parser.parse(operand);
                Memory.writeWord(address, value);
            } catch (Assembler.AsmException e) {
                System.err.println("Unresolved sybols: " + operand);
                unresolved++;
            }
        }
        return unresolved;
    }

    private static boolean isRegisterName(String name) {
        Register register = Register.valueOf(name);
        return register != null;
    }

    public static void dump(PrintStream output) {
        for (String name : symbols.keySet()) {
            if (!isRegisterName(name)) {
                int value = symbols.get(name);
                String nameImage = String.format("%8s: ", name);
                String hexImage = "0x" + String.format("%04x", value).toUpperCase();
                String decImage = String.format(" (%d)", value);
                output.println(nameImage + hexImage + decImage);
            }
        }
    }

    public static void dump(String filename) throws IOException {
        dump(new PrintStream(new File(filename)));
    }

    public static void dump() {
        dump(System.out);
    }
}

