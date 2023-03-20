package lexer;

public class Identifier extends Value {
    static StringBuilder current = new StringBuilder();

    private String name;

    public Identifier(int line, int column, String name) {
        super(line, column, Type.IDENTIFIER);
        this.name = name;
    }

    public Identifier(int line, int column, int length) {
        super(line, column, Type.IDENTIFIER);
        this.name = current.substring(0, length);
        current = new StringBuilder();
    }

    public Identifier(int line, int column) {
        super(line, column, Type.IDENTIFIER);
        this.name = current.toString();
        current = new StringBuilder();
    }

    @Override
    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean position) {
        return super.toString(kind.name() + " " + name, position);
    }

    public static String parseName(String s) {
        IdState state = IdState.START;
        String value = "";

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            state = (IdState) state.next(c);

            switch (state) {
                case CHARACTER:
                    value += c;
                    break;
                case UNDERSCORE:
                    value += '_';
                    break;
            }
        }

        if (!state.accept()) {
            throw new RuntimeException("Invalid identifier: " + s);
        }

        return value;
    }

    public static void main(String[] args) {
        for (String arg : args) {
            try {
                System.out.println(arg + ": " + parseName(arg));
            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }
        }
    }


    // IDENTIFIER FSM

    protected enum IdState implements State {
        START(false) {
            @Override
            public State next(char c) {
                current = new StringBuilder();
                if (isLetter(c)) {
                    current.append(c);
                    return CHARACTER;
                }
                return Symbol.ERROR;
            }
        },

        CHARACTER(true) {
            @Override
            public State next(char c) {
                if (isLetter(c) || isDigit(c)) {
                    current.append(c);
                    return CHARACTER;
                }
                switch (c) {
                    case '_':
                        current.append('_');
                        return UNDERSCORE;
                    default: return Symbol.ERROR;
                }
            }
        },

        UNDERSCORE(false) {
            @Override
            public State next(char c) {
                if (isLetter(c) || isDigit(c)) {
                    current.append(c);
                    return CHARACTER;
                }
                return Symbol.ERROR;
            }
        };

        private final boolean accept;
        public boolean accept() {
            return this.accept;
        }

        IdState(boolean accept) {
            this.accept = accept;
        }

        private static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }

        private static boolean isLetter(char c) {
            return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
        }
    }
}
