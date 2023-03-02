import java.io.IOException;

public class asm {

    private static String basename(String filename) {
        int index = filename.lastIndexOf('.');
        if (index > 0) {
            return filename.substring(0, index);
        } else {
            return filename;
        }
    }

	private static int assemble() {
		return Assembler.processFile(System.in);
	}

    private static int assemble(String filename) {
        try {
            return Assembler.processFile(filename);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 1;
        }
    }

    private static void save(String filename) {
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

    private static void dump(String filename) {
        try {
            if (filename == null || filename.equals("-")) {
                Symbols.dump();
            } else {
                Symbols.dump(filename);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    

    private static boolean isOption(String arg) {
        if (arg.length() > 1) {
            return arg.startsWith("-") || arg.startsWith("+");
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        boolean statistics = false;
        String output = null;
        String symbols = null;
        String option = "";
        int errors = 0;

        Assembler.initialize();
        for (String arg : args) {
            if (isOption(arg) && isOption(option)) {
                System.err.println("Missing value for option " + option);
                option = "";
                errors ++;
            }

            switch (arg.toLowerCase()) {
                case "-trace":
                    Assembler.trace = true;
                    continue;

                case "+trace":
                    Assembler.trace = false;
                    continue;

                case "-stats":
                case "-statistis":
                    statistics = true;
                    continue;

                case "+stats":
                case "+statistis":
                    statistics = false;
                    continue;

                case "-stdin":
                    errors += assemble();
                    continue;

                case "-stdout":
                    output = "-";
                    continue;

                case "-o":
                case "-output":
                    option = arg;
                    continue;

                case "-s":
                case "-symbols":
                    option = arg;
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

            switch (option.toLowerCase()) {
                case "-o":
                case "-output":
                    output = arg;
                    break;

                case "-s":
                case "-symbols":
                    symbols = arg;
                    break;

                default:
                    if (output == null && !arg.equals("-")) {
                        output = basename(arg) + ".o";
                    }
                    errors += assemble(arg);
                    break;
            }

            option = "";
        }

        if (isOption(option)) {
            System.err.println("Missing value for option " + option);
            errors ++;
        }

        errors += Assembler.finish();
        if (errors == 0) save(output);
        if (symbols != null) dump(symbols);

        if (statistics) {
            System.out.println("Errors: " + errors);
            System.out.println("Bytes: " + Assembler.bytesGenerated);
            System.out.println("Instructions: " + Assembler.instructionsGenerated);
        }
    }


    public static void help() {
        System.out.println();
        System.out.println("  java asm <options> <files>");
        System.out.println();
        System.out.println("    Assemble a TINY source file; valid options are:");
        System.out.println();
        System.out.println("    -trace            Enable tracing of each instruction assembled");
        System.out.println("    +trace            Disable tracing of each instruction assembled");
        System.out.println("    -statistics       Enable printing of assembly statistics");
        System.out.println("    +statistics       Disable printing of assembly statistics");
        System.out.println("    -output <file>    Output file for generated code");
        System.out.println("    +stdout           Send generated code to standard output");
        System.out.println("    -symbols <file>   Output file for symbol table");
        System.out.println("    +symbols          Send symbol table to standard output");
        System.out.println("    -statistics       Display assembly statistics");
        System.out.println("    +statistics       Dont display assembly statistics");
        System.out.println("    -stdin            Assemble source code from standard input");
        System.out.println("    <files>           One or more assembly source files");
        System.out.println();
        System.out.println("    By default, the output object code file name will be the name of the");
        System.out.println("       first source file with the .asm suffix replaced with a .o suffix");
        System.out.println();
    }
}
