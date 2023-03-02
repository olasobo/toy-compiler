public class Int extends Value {
    static int current = 0;

    private int value;

    public Int(int line, int column, String value) {
        super(line, column, Type.INTEGER);
        this.value = parseValue(value);
    }

    public Int(int line, int column) {
        super(line, column, Type.INTEGER);
        this.value = current;
        current = 0;
    }

    public static int parseValue(String s) {
        IntState state = IntState.START;
        int value = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            state = (IntState) state.next(c);

            switch (state) {
                case D_DIGIT:
                    value = 10 * value + digit(c);
                    break;
                case H_DIGIT:
                    value = 16 * value + digit(c);
                    break;
            }
        }

        if (!state.accept()) {
            throw new NumberFormatException("Invalid number: " + s);
        }

        return value;
    }

    public static void main(String[] args) {
        for (String arg : args) {
            try {
                System.out.println(arg + ": " + parseValue(arg));
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
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

    private static int digit(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;

        throw new IllegalArgumentException();
    }


    // INTEGER FSM

    protected enum IntState implements State {
        START(false) {
            @Override
            public State next(char c) {
                current = 0;
                if (c == '0') return ZERO;
                else if (isDecimal(c)) {
                    current = 10 * current + digit(c);
                    return D_DIGIT;
                }
                return Symbol.ERROR;
            }
        },

        ZERO(true) {
            @Override
            public State next(char c) {
                if (isDecimal(c)) {
                    current = 10 * current + digit(c);
                    return D_DIGIT;
                }

                switch (c) {
                    case 'x':
                    case 'X': return HEXADECIMAL;
                    case '_': return D_UNDERSCORE;
                    default: return Symbol.ERROR;
                }
            }
        },

        D_DIGIT(true) {
            @Override
            public State next(char c) {
                if (isDecimal(c)) {
                    current = 10 * current + digit(c);
                    return D_DIGIT;
                }

                switch (c) {
                    case '_': return D_UNDERSCORE;
                    default:  return Symbol.ERROR;
                }
            }
        },

        D_UNDERSCORE(false) {
            @Override
            public State next(char c) {
                if (isDecimal(c)) {
                    current = 10 * current + digit(c);
                    return D_DIGIT;
                } else return Symbol.ERROR;
            }
        },

        HEXADECIMAL(false) {
            @Override
            public State next(char c) {
                if (isHexadecimal(c)) {
                    current = 16 * current + digit(c);
                    return H_DIGIT;
                }
                return Symbol.ERROR;
            }
        },

        H_DIGIT(true) {
            @Override
            public State next(char c) {
                if (isHexadecimal(c)) {
                    current = 16 * current + digit(c);
                    return H_DIGIT;
                }

                switch (c) {
                    case '_': return H_UNDERSCORE;
                    default: return Symbol.ERROR;
                }
            }
        },

        H_UNDERSCORE(false) {
            @Override
            public State next(char c) {
                if (isHexadecimal(c)) {
                    current = 16 * current + digit(c);
                    return H_DIGIT;
                }
                return Symbol.ERROR;
            }
        };

        private final boolean accept;
        public boolean accept() {
            return this.accept;
        }

        IntState(boolean accept) {
            this.accept = accept;
        }

        private static boolean isDecimal(char c) {
            try {
                return digit(c) < 10;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        private static boolean isHexadecimal(char c) {
            try {
                return digit(c) < 16;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}
