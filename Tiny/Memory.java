import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;

public class Memory {

    public static final int WORD_SIZE = 16;
    public static final boolean BIG_ENDIAN = false;

    public static final int SIZE = 1 << WORD_SIZE;
    private static final byte[] memory = new byte[SIZE];

    public static int reads = 0;
    public static int writes = 0;

    public static int readByte(int address) {
        Memory.reads++;
        return memory[address] & 0xFF;
    }

    public static int readWord(int address) {
        if (BIG_ENDIAN){
            int hi = readByte(wrap(address));
            int lo = readByte(wrap(address+1));
            return word(lo, hi);
        } else {
            int lo = readByte(wrap(address));
            int hi = readByte(wrap(address+1));
            return word(lo, hi);
        }
    }

    public static void writeByte(int address, int value) {
        Memory.writes++;
        memory[address] = (byte) (value & 0xFF);
    }

    public static void writeWord(int address, int value) {
        if (BIG_ENDIAN) {
            writeByte(wrap(address), hi(value));
            writeByte(wrap(address+1), lo(value));
        } else {
            writeByte(wrap(address), lo(value));
            writeByte(wrap(address+1), hi(value));
        }
    }

    public static void push(int value) {
        writeWord(Register.SP.preDecrement(2), value);
    }

    public static int pop() {
        return readWord(Register.SP.postIncrement(2));
    }

    public static void clear() {
        for (int address = 0; address < Memory.SIZE; address++) {
            memory[address] = 0;
        }
    }

    private static boolean isAllZero(int address, int count) {
        for (int i = 0; i < count; i++) {
            if (readByte(address+i) != 0) return false;
        }
        return true;
    }
        

    // -- Utilities -------------------------------------------------------------------

    private static int word(int lo, int hi) {
        lo &= 0xFF;
        hi &= 0xFF;
        return lo | hi << 8;
    }

    private static int lo(int word) {
        return word & 0xFF;
    }

    private static int hi(int word) {
        return (word >> 8) & 0xFF;
    }

    private static int wrap(int value) {
        return value & 0xFFFF;
    }

    private static int roundup(int value, int multiple) {
        return multiple * ((value + multiple - 1) / multiple);
    }

    private static String hex2(int value) {
        return String.format("%02x", value & 0xFF).toUpperCase();
    }

    private static String hex4(int value) {
        return String.format("%04x", value & 0xFFFF).toUpperCase();
    }


    // -- Load (from file) -------------------------------------------------------

    public static void load(Scanner input) {
        int address = 0;

        while (input.hasNextLine()) {
            String line = input.nextLine();
            int colon = line.indexOf(':');
            if (colon > 0) {
                String addr = line.substring(0, colon);
                address = Integer.parseInt(addr, 16);
                line = line.substring(colon+1);
            }
            Scanner scanner = new Scanner(line);
            while (scanner.hasNextInt(16)) {
                Memory.writeByte(address++, scanner.nextInt(16));
            }
        }
    }

    public static void load(InputStream input) {
        load(new Scanner(input));
    }

    public static void load(String filename) throws IOException {
        load(new Scanner(new File(filename)));
    }

    public static void load() {
        load(System.in);
    }

    // -- Save (to file) ---------------------------------------------------------

    public static void save(PrintStream output) {
        int address = 0;
        int width = 16;

        while (address < Memory.SIZE) {
            if (!isAllZero(address, width)) {
                output.print(hex4(address));
                String separator = ": ";
                for (int i = 0; i < width; i++) {
                    int value = Memory.readByte(address+i);
                    output.print(separator);
                    output.print(hex2(value));
                    separator = (i % 2 == 0) ? " " : "  ";
                }
                output.println();
            }
            address += width;
        }
    }

    public static void save(String filename) throws IOException {
        save(new PrintStream(filename));
    }

    public static void save() {
        save(System.out);
    }
}
