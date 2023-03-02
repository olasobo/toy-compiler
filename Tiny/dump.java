import java.io.IOException;

public class dump {

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


    private static boolean isAllZero(int[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != 0) return false;
        }
        return true;
    }

    private static void dump(
        int     start,
		int     count,
        int     width,
        boolean dumpAddress,
        boolean dumpBytes,
        boolean bytesInDecimal,
        boolean dumpWords,
        boolean wordsInDecimal,
        boolean dumpChars
    ) {
        width = roundUpToNextPowerOfTwo(width);
        int stop = wrap(roundUpToNextMultiple(start+count, width)-1);
        int address = wrap(roundDownToNextMultiple(start, width));
        boolean skip = false;

        while (address <= stop) {
            int[] bytes = Disassembler.readBytes(address, width);
            String separator = "";
            String line = "";

            if (dumpAddress) {
                line += Disassembler.formatAddress(address);
                separator = skip ? "* " : ": ";
            }

            if (dumpBytes) {
                line += separator;
                line += Disassembler.formatBytes(bytes, bytesInDecimal);
                separator = "  |  ";
            }

            if (dumpWords) {
                line += separator;
                int[] words = Disassembler.readWords(address, width/2);
                line += Disassembler.formatWords(words, wordsInDecimal);
                separator = "  |  ";
            }

            if (dumpChars) {
                line += separator;
                line += Disassembler.formatChars(bytes);
                separator = "  |  ";
            }

            if (!dumpAddress) {
                System.out.println(line);
            } else if (!isAllZero(bytes)) {
                System.out.println(line);
                skip = false;
            } else {
                skip = true;
            }
            address += width;
        }
    }


    public static void main(String[] args) {
        String option = "";
        boolean dumpAddress = true;
        boolean dumpBytes = true;
        boolean bytesInDecimal = false;
        boolean dumpWords = false;
        boolean wordsInDecimal = true;
        boolean dumpChars = false;
        boolean decimal = false;
        boolean dump = false;
        int width = 16;
        int start = 0;
        int count = Memory.SIZE;
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

                case "-byte":
                case "-bytes":
                    dumpBytes = true;
                    continue;

                case "+byte":
                case "+bytes":
                    dumpBytes = false;
                    continue;

                case "-word":
                case "-words":
                    dumpWords = true;
                    continue;

                case "+word":
                case "+words":
                    dumpWords = false;
                    continue;

                case "-char":
                case "-chars":
                case "-ascii":
                    dumpChars = true;
                    continue;

                case "+char":
                case "+chars":
                case "+ascii":
                    dumpChars = false;
                    continue;

                case "-hex":
                case "+decimal":
                    decimal = false;
                    continue;

                case "+hex":
                case "-decimal":
                    decimal = true;
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

		if (decimal) {
			wordsInDecimal = true;
			bytesInDecimal = !dumpWords;
		} else {
			bytesInDecimal = false;
			wordsInDecimal = false;
		}

		dump(
			start,
			count,
			width,
			dumpAddress,
			dumpBytes,
			bytesInDecimal,
			dumpWords,
			wordsInDecimal,
			dumpChars
		);
    }

    
    public static void help() {
        System.out.println();
        System.out.println("  java dump <options> <files>");
        System.out.println();
        System.out.println("    Dump a the contents of TINY object code files:");
        System.out.println();
        System.out.println("    -width #            The number of bytes to display on each line");
        System.out.println("    -count #            The maximum number of bytes to display");
        System.out.println("    -begin #            The address at which to begin dumping code");
        System.out.println("    -address            Display the address at the beginning of each line");
        System.out.println("    -bytes              Display the object code as bytes");
        System.out.println("    -words              Display the object code as words");
        System.out.println("    -chars              Display the object code as chars");
        System.out.println("    +address            Dont display the address");
        System.out.println("    +words              Dont display the object code as words");
        System.out.println("    +chars              Dont display the object code as chars");
        System.out.println("    -hex                Dump the object code in hex");
        System.out.println("    -decimal            Dump the object code in decimal");
        System.out.println("    -stdin              Read object code file from standard input");
        System.out.println("    <files>             One or more TINY object code files to be dumped");
        System.out.println();
        System.out.println("       Multiple formats (-bytes, -words, -chars) can be specified");
        System.out.println("           simultaneously and the data will be displayed in all of");
        System.out.println("           the selected formats on each line");

    }
}
