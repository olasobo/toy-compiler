public class Parser {

    // The expression parser for operands.  All operators have the same precidence
    // and are evaluated from left to right. Parentheses are not allowed.
    //
    // Currently there are not fixups for forward references in expressions.
    // Fixups are only generated for the usage of a stand-alone identifier.
    //
    // Expressions are composed of
    //
    //   Identifiers
    //   Decimal numbers (e.g. 1_000_000);
    //   Hexadecimal numbers (e.g., 0x7FFF_FFFF)
    //   Octal numbers (leading zero: 072);
    //   Binary numbers (e.g. 0b0101_0011)
    //   Character literals (e.g., 'z')
    //      Escapes: \t \n \r \0 \' \\
    //   Unary prefix operators: + - ! ~
    //   Binary infix operators: + - * / % & | ^ < >
    //      Shift left: <
    //      Shift right: >


    // -- Finite State Machine --------------------------------------------------------

    public static enum State {
        START(false) {
            @Override
            public State next(char c) {
                if (c == '.') return DOT;
                if (c == '0') return ZERO;
                if (isQuote(c)) return QUOTE1;
                if (isLetter(c)) return NAME;
                if (isDigit(c)) return DEC_DIGIT;
                if (isUnaryOp(c)) return UNARY_OPERATOR;
                return ERROR;
            }
        },

        UNARY_OPERATOR(false) {
            @Override
            public State next(char c) {
                if (c == '.') return DOT;
                if (c == '0') return ZERO;
                if (isQuote(c)) return QUOTE1;
                if (isLetter(c)) return NAME;
                if (isDigit(c)) return DEC_DIGIT;
                return ERROR;
            }
        },

        BINARY_OPERATOR(false) {
            public State next(char c) {
                if (c == '.') return DOT;
                if (c == '0') return ZERO;
                if (isQuote(c)) return QUOTE1;
                if (isLetter(c)) return NAME;
                if (isDigit(c)) return DEC_DIGIT;
                if (isUnaryOp(c)) return UNARY_OPERATOR;
                return ERROR;
            }
        },

        DOT(true) {
            @Override
            public State next(char c) {
                if (isBinaryOp(c)) return BINARY_OPERATOR;
                return ERROR;
            }
        },

        NAME(true) {
            @Override
            public State next(char c) {
                if (c == '_') return NAME;
                if (isDigit(c)) return NAME;
                if (isLetter(c)) return NAME;
                if (isBinaryOp(c)) return BINARY_OPERATOR;
                return ERROR;
            }
        },

        ZERO(true) {
            @Override
            public State next(char c) {
                switch (c) {
                    case 'x': case 'X': return HEX_MARK;
                    case 'b': case 'B': return BIN_MARK;
                    default:
                        if (isOctDigit(c)) return OCT_DIGIT;
                        if (isBinaryOp(c)) return BINARY_OPERATOR;
                        return ERROR;
                }
            }
        },

        HEX_MARK(false) {
            @Override
            public State next(char c) {
                if (isHexDigit(c)) return HEX_DIGIT;
                return ERROR;
            }
        },

        HEX_DIGIT(true) {
            @Override
            public State next(char c) {
                if (c == '_') return HEX_UNDER;
                if (isHexDigit(c)) return HEX_DIGIT;
                if (isBinaryOp(c)) return BINARY_OPERATOR;
                return ERROR;
            }
        },

        HEX_UNDER(false) {
            @Override
            public State next(char c) {
                if (isHexDigit(c)) return HEX_DIGIT;
                return ERROR;
            }
        },

        DEC_DIGIT(true) {
            @Override
            public State next(char c) {
                if (c == '_') return DEC_UNDER;
                if (isDigit(c)) return DEC_DIGIT;
                if (isBinaryOp(c)) return BINARY_OPERATOR;
                return ERROR;
            }
        },

        DEC_UNDER(false) {
            @Override
            public State next(char c) {
                if (isDigit(c)) return DEC_DIGIT;
                return ERROR;
            }
        },

        OCT_DIGIT(true) {
            @Override
            public State next(char c) {
                if (c == '_') return OCT_UNDER;
                if (isOctDigit(c)) return OCT_DIGIT;
                if (isBinaryOp(c)) return BINARY_OPERATOR;
                return ERROR;
            }
        },

        OCT_UNDER(false) {
            @Override
            public State next(char c) {
                if (isOctDigit(c)) return OCT_DIGIT;
                return ERROR;
            }
        },

        BIN_MARK(false) {
            @Override
            public State next(char c) {
                if (isBinDigit(c)) return BIN_DIGIT;
                return ERROR;
            }
        },

        BIN_DIGIT(true) {
            @Override
            public State next(char c) {
                if (c == '_') return BIN_UNDER;
                if (isBinDigit(c)) return BIN_DIGIT;
                if (isBinaryOp(c)) return BINARY_OPERATOR;
                return ERROR;
            }
        },

        BIN_UNDER(false) {
            @Override
            public State next(char c) {
                if (isBinDigit(c)) return BIN_DIGIT;
                return ERROR;
            }
        },


        QUOTE1(false) {
            @Override
            public State next(char c) {
                if (c == '\\') return ESCAPE;
                if (isControl(c)) return ERROR;
                return CHAR;
            }
        },

        ESCAPE(false) {
            @Override
            public State next(char c) {
                switch (c) {
                    case '\\':
                    case '\'':
                    case 't':
                    case 'n':
                    case 'r':
                    case '0':
                        return CHAR;

                    default:
                        return ERROR;
                }
            }
        },

        CHAR(false) {
            @Override
            public State next(char c) {
                if (c == '\'') return QUOTE2;
                return ERROR;
            }
        },

        QUOTE2(true) {
            @Override
            public State next(char c) {
                if (isBinaryOp(c)) return BINARY_OPERATOR;
                return ERROR;
            }
        },

        ERROR(false);


        private boolean accept;

        private State(boolean accept) {
            this.accept = accept;
        }

        public State next(char c) {
            return ERROR;
        }
    }


    // -- FSM Expression Parser -------------------------------------------------------

    public static int parse(String s) {
        State state = State.START;
        boolean escape = false;
        char operator = ' ';
        char unary = '+';
        int current = 0;
        int value = 0;
        int radix = 10;
        int undefined = 0;
        String name = "";

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            state = state.next(c);

            switch (state) {
                case UNARY_OPERATOR:
                    unary = c;
                    break;

                case BINARY_OPERATOR:
                    if (isUndefined(name)) undefined++;
                    current = evaluate(unary, current, name);
                    value = evaluate(operator, value, current);
                    operator = c;
                    unary = '+';
                    current = 0;
                    name = "";
                    break;

                case DOT:
                    current = Assembler.dot();
                    break;

                case NAME:
                    name += c;
                    break;

                case ZERO:
                    current = 0;
                    radix = 8;
                    break;

                case BIN_MARK:
                    current = 0;
                    radix = 2;
                    break;

                case HEX_MARK:
                    current = 0;
                    radix = 16;
                    break;

                case BIN_DIGIT:
                case OCT_DIGIT:
                case DEC_DIGIT:
                case HEX_DIGIT:
                    current = radix * current + digit(c);
                    break;

                case QUOTE1:
                    escape = false;
                    break;

                case ESCAPE:
                    escape = true;
                    break;

                case CHAR:
                    current = escape ? escape(c) : (int) c;
                    break;
            }
        }

        switch (state) {
            case DOT:
            case NAME:
            case ZERO:
            case BIN_DIGIT:
            case OCT_DIGIT:
            case DEC_DIGIT:
            case HEX_DIGIT:
            case QUOTE2:
                if (isUndefined(name)) undefined++;
                current = evaluate(unary, current, name);
                if (undefined > 0) {
                    throw new Assembler.Undefined(s);
                }
                return evaluate(operator, value, current);

            default:
                throw new Assembler.BadSyntax(s);
        }
    }


    // -- Evaluation ------------------------------------------------------------------

    private static boolean isUndefined(String name) {
        return name != null && name.length() > 0 && !Symbols.isDefined(name);
    }

    private static int evaluate(char operator, int left, int right) {
        switch (operator) {
            case '+': return left + right;
            case '-': return left - right;
            case '*': return left * right;
            case '/': return left / right;
            case '%': return left % right;
            case '&': return left & right;
            case '|': return left | right;
            case '^': return left ^ right;
            case '<': return left << right;
            case '>': return left >> right;
            default:  return right;
        }
    }

    private static int evaluate(char operator, int value, String name) {
        if (name.length() > 0) {
            if (Symbols.isDefined(name)) {
                value = Symbols.get(name);
            } else {
                value = 0;
            }
        }

        switch (operator) {
            case '+': return +value;
            case '-': return -value;
            case '~': return ~value;
            default:  return value;
        }
    }


    // -- Utilities -------------------------------------------------------------------

    private static boolean isControl(char c) {
        return c < 32;
    }

    private static boolean isQuote(char c) {
        return c == '\'';
    }

    private static boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private static boolean isDigit(char c) {
        return c >= '0' &&  c <= '9';
    }

    private static boolean isBinDigit(char c) {
        return c == '0' || c == '1';
    }

    private static boolean isOctDigit(char c) {
        return c >= '0' &&  c <= '7';
    }

    private static boolean isDecDigit(char c) {
        return c >= '0' &&  c <= '9';
    }

    private static boolean isHexDigit(char c){
        if (c >= '0' && c <= '9') return true;
        if (c >= 'a' && c <= 'f') return true;
        if (c >= 'A' && c <= 'F') return true;
        return false;
    }

    private static int digit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        } else {
            throw new IllegalArgumentException("Invalid digit");
        }
    }

    private static boolean isUnaryOp(char c) {
        switch (c) {
            case '+': return true;
            case '-': return true;
            case '~': return true;
            default:  return false;
        }
    }

    private static boolean isBinaryOp(char c) {
        switch (c) {
            case '+': return true;
            case '-': return true;
            case '*': return true;
            case '/': return true;
            case '%': return true;
            case '&': return true;
            case '|': return true;
            case '^': return true;
            case '<': return true;
            case '>': return true;
            default:  return false;
        }
    }

    private static int escape(char c) {
        switch (c) {
            case '\\': return '\\';
            case '\'': return '\'';
            case 't':  return '\t';
            case 'n':  return '\n';
            case 'r':  return '\r';
            case '0':  return '\000';
            default:   throw new IllegalArgumentException("Invalid escape character");
        }
    }

}
