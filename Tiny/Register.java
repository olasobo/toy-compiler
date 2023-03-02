import java.io.PrintStream;

public class Register {

    public static final int REGISTERS = 16;

    public static final Register R0  = new Register("R0",  0);
    public static final Register R1  = new Register("R1",  1);
    public static final Register R2  = new Register("R2",  2);
    public static final Register R3  = new Register("R3",  3);
    public static final Register R4  = new Register("R4",  4);
    public static final Register R5  = new Register("R5",  5);
    public static final Register R6  = new Register("R6",  6);
    public static final Register R7  = new Register("R7",  7);
    public static final Register R8  = new Register("R8",  8);
    public static final Register R9  = new Register("R9",  9);
    public static final Register R10 = new Register("R10", 10);
    public static final Register R11 = new Register("R11", 11);
    public static final Register R12 = new Register("R12", 12);
    public static final Register R13 = new Register("R13", 13);
    public static final Register R14 = new Register("R14", 14);
    public static final Register R15 = new Register("R15", 15);

    public static final Register PC = new Register("PC", -1);
    public static final Register SP = R15;

    public static final Register[] registers = {
        R0,  R1,  R2,  R3,  R4,  R5,  R6,  R7,
        R8,  R9,  R10, R11, R12, R13, R14, R15
    };


    private String name;
    private int number;
    private int value;

    private Register(String name, int number) {
        this.number = number;
        this.name = name;
        this.value = 0;
    }

    public String name() {
        return this.name;
    }

    public int number() {
        return this.number;
    }

    public int get() {
        return this.value;
    }

    public void set(int value) {
        this.value = value & 0xFFFF;
    }

    public void clear() {
        this.value = 0;
    }

    public int increment() {
        return this.preIncrement();
    }

    public int decrement() {
        return this.preDecrement();
    }

    public int preIncrement() {
        return this.preIncrement(1);
    }

    public int postIncrement() {
        return this.postIncrement(1);
    }

    public int preDecrement() {
        return this.preDecrement(1);
    }

    public int postDerement() {
        return this.postDecrement(1);
    }

    public int preIncrement(int amount) {
        this.set(this.value + amount);
        return this.value;
    }

    public int postIncrement(int amount) {
        int result = this.value;
        this.set(this.value + amount);
        return result;
    }

    public int preDecrement(int amount) {
        this.set(this.value - amount);
        return this.value;
    }

    public int postDecrement(int amount) {
        int result = this.value;
        this.set(this.value - amount);
        return result;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    public static Register valueOf(int number) {
        return Register.registers[number];
    }

    public static Register valueOf(String s) {
        s = s.toUpperCase();
        for (Register register : Register.registers) {
            if (register.name.equals(s)) return register;
        }
        if (s.equals("SP")) {
            return Register.SP;
        } else {
            return null;
        }
    }

    public static void reset() {
        for (Register register : Register.registers) {
            register.value = 0;
        }
        Flags.reset();
    }


    public static class Flags {

        public static boolean zero = false;
        public static boolean carry = false;
        public static boolean negative = false;
        public static boolean halted = false;

        public static boolean isEqual() {
            return zero;
        }

        public static boolean isNotEqual() {
            return !zero;
        }

        public static boolean isLess(boolean signed) {
            if (signed) {
                return negative;
            } else {
                return carry;
            }
        }

        public static boolean isLessEqual(boolean signed) {
            if (signed) {
                return negative | zero;
            } else {
                return carry | zero;
            }
        }

        public static boolean isGreaterEqual(boolean signed) {
            if (signed) {
                return !negative;
            } else {
                return !carry;
            }
        }

        public static boolean isGreater(boolean signed) {
            if (signed){
                return !negative & !zero;
            } else {
                return !carry & !zero;
            }
        }

        public static String toString(boolean verbose) {
            String separator = "";
            String result = "";
            if (verbose) {
                if (zero) {
                    result += separator + "zero";
                    separator = ", ";
                }
                if (negative) {
                    result += separator + "negative";
                    separator = ", ";
                }
                if (carry) {
                    result += separator + "carry";
                    separator = ", ";
                }
                if (halted) {
                    result += separator + "halted";
                    separator = ", ";
                }
            } else {
                if (zero) result += "Z";
                if (negative) result += "N";
                if (carry) result += "C";
                if (halted) result += "H";
            }

            if (result.length() == 0) {
                return "none";
            } else {
                return result;
            }
        }

        public static void reset() {
            zero = false;
            carry = false;
            negative = false;
            halted = false;
        }
    }


    private static String hex(int value) {
        return String.format("%04x", value).toUpperCase();
    }

    public static void dump(PrintStream output) {
        String separator = "";
        String result = "";
        int count = 0;

        for (Register register : Register.registers) {
            output.print(separator);
            output.print(String.format("%3s = ", register.name));
            output.print(hex(register.value));
            if (++count % 4 == 0) {
                separator = "\n";
            } else {
                separator = "  ";
            }
        }
        output.println();
        output.print(" PC = " + hex(PC.value) + ": ");
        output.println(Disassembler.formatInstruction(PC.value));
        output.println(" FLAGS: " + Flags.toString(true));
    }

    public static void dump() {
        dump(System.out);
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            Register.registers[i].value = Integer.parseInt(args[i]);
        }
        Register.dump();
    }
}
