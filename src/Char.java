public class Char extends Value {
    static char current = '\0';

    private char value;

    public Char(int line, int column, String value) {
        super(line, column, Type.CHARACTER);
        this.value = parseValue(value);
    }

    public Char(int line, int column) {
        super(line, column, Type.CHARACTER);
        this.value = current;
        current = '\0';
    }

    public static char parseValue(String s) {
        CharState state = CharState.START;
        Character value = null;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            state = (CharState) state.next(c);

            switch (state) {
                case CHARACTER:
                    value = c;
                    break;
                case SPECIAL_CHARACTER:
                    value = special(c);
                    break;
            }
        }

        if (!state.accept() || value == null) {
            throw new RuntimeException("Invalid character: " + s);
        }

        return value;
    }

    public static void main(String[] args) {
        for (String arg : args) {
            for (String c : arg.split(" ")) {
                try {
                    System.out.println(c + ": " + parseValue(c));
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


    // CHARACTER FSM

    protected enum CharState implements State {
        START(false) {
            @Override
            public State next(char c) {
                if (c == '\'') return FIRST_QUOTE;
                return Symbol.ERROR;
            }
        },

        FIRST_QUOTE(false) {
            @Override
            public State next(char c) {
                switch (c) {
                    case '\n':
                    case '\'':  return Symbol.ERROR;
                    case '\\':  return BACKSLASH;
                    default:
                        current = c;
                        return CHARACTER;
                }
            }
        },

        BACKSLASH(false) {
            @Override
            public State next(char c) {
                if (isSpecial(c)) {
                    current = special(c);
                    return SPECIAL_CHARACTER;
                }
                return Symbol.ERROR;
            }
        },

        CHARACTER(false) {
            @Override
            public State next(char c) {
                if (c == '\'') return FINAL_QUOTE;
                return Symbol.ERROR;
            }
        },

        SPECIAL_CHARACTER(false) {
            @Override
            public State next(char c) {
                if (c == '\'') return FINAL_QUOTE;
                return Symbol.ERROR;
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

        CharState(boolean accept) {
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
