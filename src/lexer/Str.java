package lexer;

public class Str extends Value {
    static StringBuilder current = new StringBuilder();

    private String value;

    public Str(int line, int column, String value) {
        super(line, column, Type.STRING);
        this.value = parseValue(value);
    }

    public Str(int line, int column, int length) {
        super(line, column, Type.STRING);
        this.value = current.substring(0, length);
        current = new StringBuilder();
    }

    public Str(int line, int column) {
        super(line, column, Type.STRING);
        this.value = current.toString();
        current = new StringBuilder();
    }

    public static String parseValue(String s) {
        StrState state = StrState.START;
        StringBuilder value = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            state = (StrState) state.next(c);

            switch (state) {
                case CHARACTER:
                    value.append(c);
                    break;
                case SPECIAL_CHARACTER:
                    value.append(special(c));
                    break;
            }
        }

        if (!state.accept() || value == null) {
            throw new RuntimeException("Invalid string: " + s);
        }

        return value.toString();
    }

    public static void main(String[] args) {
        for (String arg : args) {
            for (String s : arg.split(",")) {
                System.out.println(s);
                try {
                    System.out.println(s + ": " + parseValue(s));
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean position) {
        return super.toString(kind.name() + " " + value, position);
    }

    private static char special(char c) {
        String characters = "\\\'\"rnt0";
        String equivalent = "\\\'\"\r\n\t\0";
        for (int i = 0; i < characters.length(); i++) {
            if (characters.charAt(i) == c) return equivalent.charAt(i);
        }
        throw new IllegalArgumentException();
    }


    // STRING FSM

    protected enum StrState implements State {
        START(false) {
            @Override
            public State next(char c) {
                if (c == '\"') return FIRST_QUOTE;
                return Symbol.ERROR;
            }
        },

        FIRST_QUOTE(false) {
            @Override
            public State next(char c) {
                switch (c) {
                    case '\n': return Symbol.ERROR;
                    case '\"': return FINAL_QUOTE;
                    case '\\': return BACKSLASH;
                    default:
                        current.append(c);
                        return CHARACTER;
                }
            }
        },

        BACKSLASH(false) {
            @Override
            public State next(char c) {
                if (isSpecial(c)) {
                    current.append(special(c));
                    return SPECIAL_CHARACTER;
                }
                return Symbol.ERROR;
            }
        },

        CHARACTER(false) {
            @Override
            public State next(char c) {
                switch (c) {
                    case '\n': return Symbol.ERROR;
                    case '\"': return FINAL_QUOTE;
                    case '\\': return BACKSLASH;
                    default:
                        current.append(c);
                        return CHARACTER;
                }
            }
        },

        SPECIAL_CHARACTER(false) {
            @Override
            public State next(char c) {
                switch (c) {
                    case '\n': return Symbol.ERROR;
                    case '\"': return FINAL_QUOTE;
                    case '\\': return BACKSLASH;
                    default:
                        current.append(c);
                        return CHARACTER;
                }
            }
        },

        FINAL_QUOTE(true) {
            @Override
            public State next(char c) {
                return Symbol.ERROR;
            }
        };

        private final boolean accept;
        public boolean accept() {
            return this.accept;
        }

        StrState(boolean accept) {
            this.accept = accept;
        }

        private static boolean isSpecial(char c) {
            try {
                special(c);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
