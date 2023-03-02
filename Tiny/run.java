import java.io.IOException;

public class run {

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
        return Integer.parseInt(s, radix);
    }

    private static void load(String filename) throws IOException {
        if (filename == null || filename.equals("-")) {
            Memory.load();
        } else {
            Memory.load(filename);
        }
    }

    private static void dump(String filename) {
        try {
            if (filename == null || filename.equals("-")) {
                Memory.save();
            } else {
                Memory.save(filename);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static boolean isOption(String arg) {
        if (arg.length() > 0) {
            return arg.startsWith("-") || arg.startsWith("+");
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        boolean statistics = false;
        String dump = null;
        String option = "";
        int errors = 0;
        int start = 0;

        for (String arg : args) {
            if (isOption(arg) && isOption(option)) {
                System.err.println("Missing value for option " + option);
                option = "";
                errors++;
            }

            switch (arg.toLowerCase()) {
                case "-stats":
                case "-statistics":
                    statistics = true;
                    continue;

                case "+stats":
                case "+statistics":
                    statistics = false;
                    continue;

                case "-trace":
                    Execute.trace = true;
                    continue;

                case "+trace":
                    Execute.trace = false;
                    continue;

                case "-silent":
                    Execute.silent = true;
                    continue;
        
                case "+silent":
                    Execute.silent = false;
                    continue;

                case "-debug":
                    Execute.debug = true;
                    continue;

                case "+debug":
                    Execute.debug = false;
                    continue;

                case "-start":
                    option = arg;
                    continue;

                case "-dump":
                    option = arg;
                    continue;

                case "-stdin":
                    try {
                        load("-");
                    } catch(IOException e) {
                        System.err.println("Error reading object code from standard input: " + e.getMessage());
                    }
                    continue;

                case "-help":
                    help();
                    return;

                case "":
                    continue;

                default:
                    if (isOption(arg)) {
                        System.err.println("Invalid option: " + arg);
                        errors++;
                        continue;
                    }
            }

            try {
                switch (option.toLowerCase()) {
                    case "-start":
                        start = getValue(arg);
                        break;

                    case "-dump":
                        dump = arg;
                        break;

                    default:
                        load(arg);
                        break;
                }

            } catch (NumberFormatException e) {
                System.err.println("Invalid value for option " + option + ": " + arg);
                errors++;
            } catch (IOException e) {
                System.err.println("Couldn not read input file: " + e.getMessage());
                errors++;
            }

            option = "";
        }

        if (isOption(option)) {
            System.err.println("Missing value for option " + option);
            errors++;
        }

        if (errors > 0) return;

        int cycles = Execute.run(start);

        if (dump != null) {
            dump(dump);
        }

        if (statistics) {
            System.out.println();
            System.out.println("Cycles: " + cycles);
            System.out.println("Reads:  " + Memory.reads);
            System.out.println("Writes: " + Memory.writes);
        }
    }

    public static void help() {
        System.out.println();
        System.out.println("  java run <options> <file>");
        System.out.println();
        System.out.println("    Execute a TINY program; valid options are:");
        System.out.println();
        System.out.println("    -trace            Enable tracing of each executed instruction");
        System.out.println("    +trace            Disable tracing of each executed instruction");
        System.out.println("    -debug            Enable REGS and DUMP instructions");
        System.out.println("    +trace            Disable REGS and DUMP instructions");
        System.out.println("    -silent           Disable output from OUT instructions");
        System.out.println("    +silent           Enable output from OUT instructions");
        System.out.println("    -statistics       Enable printing of execution statistics");
        System.out.println("    +statistics       Enable execution tracing");
        System.out.println("    -dump <file>      Dump memory contents when HALT is executed");
        System.out.println("    -start <address>  Set the program starting address");
        System.out.println("    -stdin            Read the object code file from standard input");
        System.out.println("    <file>            The TINY object code file to be loaded into memory");
        System.out.println();
        System.out.println("    A file name of \"-\" represents standard input (or standard outupt)");
        System.out.println();
    }
        
}
