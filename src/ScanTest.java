public class ScanTest {

    private enum Kind {
        NAME {
            @Override
            public void scan(String s) {
                System.out.println(Scan.identifier(s));
            }
        },

        STRING {
            @Override
            public void scan(String s) {
                System.out.println(Scan.string(s));
            }
        },

        INTEGER {
            @Override
            public void scan(String s) {
                System.out.println(Scan.integer(s));
            }
        },

        CHARACTER {
            @Override
            public void scan(String s) {
                System.out.println(Scan.character(s));
            }
        };

        public abstract void scan(String s);
    }


    public static void main(String[] args) {
        Kind kind = Kind.INTEGER;

        for (String arg : args) {
            switch (arg) {
                case "-i":
                case "-integer":
                    kind = Kind.INTEGER;
                    continue;

                case "-c":
                case "-char":
                case "-character":
                    kind = Kind.CHARACTER;
                    continue;

                case "-s":
                case "-string":
                    kind = Kind.STRING;
                    continue;

                case "-n":
                case "-name":
                    kind = Kind.NAME;
                    continue;
            }

            try {
                System.out.print(arg + ": ");
                kind.scan(arg);
            } catch (Exception e) {
                System.err.print(e);
            }
        }
    }
}
