public enum Symbol implements State, Kind {
    START(false) {
        @Override
        public State next(char c) {
            if (isLetter(c)) return Identifier.IdState.START.next(c);
            if (isDigit(c)) return Int.IntState.START.next(c);
            switch (c) {
                case '\'':  return Char.CharState.START.next(c);
                case '\"':  return Str.StrState.START.next(c);
                case '\t':
                case ' ':   return START;
                case '-':   return MINUS;
                case '+':   return PLUS;
                case '!':   return BANG;
                case '~':   return TILDE;
                case '*':   return STAR;
                case '/':   return SLASH;
                case '%':   return PERCENT;
                case '&':   return AND;
                case '|':   return PIPE;
                case '^':   return CARET;
                case '<':   return LESS;
                case '>':   return GREATER;
                case '=':   return EQUAL;
                case '(':   return LEFT_PARENTHESIS;
                case ')':   return RIGHT_PARENTHESIS;
                case '[':   return LEFT_BRACKET;
                case ']':   return RIGHT_BRACKET;
                case '{':   return LEFT_BRACE;
                case '}':   return RIGHT_BRACE;
                case '.':   return PERIOD;
                case ',':   return COMMA;
                case ';':   return SEMICOLON;
                case ':':   return COLON;
                case '\u001a':  return END;
                default:    return ERROR;
            }
        }
    },

    PLUS(true) {
        @Override
        public Symbol next(char c) {
            switch (c) {
                case '+':   return PLUS_PLUS;
                case '=':   return PLUS_EQUAL;
                default:    return ERROR;
            }
        }
    },
    MINUS(true) {
        @Override
        public Symbol next(char c) {
            switch (c) {
                case '-':   return MINUS_MINUS;
                case '=':   return MINUS_EQUAL;
                default:    return ERROR;
            }
        }
    },
    BANG(true) {
        @Override
        public Symbol next(char c) {
            if (c == '=') return BANG_EQUAL;
            return ERROR;
        }
    },
    TILDE(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    PLUS_PLUS(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    MINUS_MINUS(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    STAR(true) {
        @Override
        public Symbol next(char c) {
            if (c == '=') return STAR_EQUAL;
            return ERROR;
        }
    },
    SLASH(true) {
        @Override
        public Symbol next(char c) {
            switch (c) {
                case '=':   return SLASH_EQUAL;
                case '/':   return SLASH_SLASH;
                default:    return ERROR;
            }
        }
    },
    PERCENT(true) {
        @Override
        public Symbol next(char c) {
            switch (c) {
                case '=':   return PERCENT_EQUAL;
                default:    return ERROR;
            }
        }
    },
    AND(true) {
        @Override
        public Symbol next(char c) {
            switch(c) {
                case '=':   return AND_EQUAL;
                case '&':   return AND_AND;
                default:    return ERROR;
            }
        }
    },
    AND_AND(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    PIPE(true) {
        @Override
        public Symbol next(char c) {
            switch(c) {
                case '=':   return PIPE_EQUAL;
                case '|':   return PIPE_PIPE;
                default:    return ERROR;
            }
        }
    },
    PIPE_PIPE(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    CARET(true) {
        @Override
        public Symbol next(char c) {
            if (c == '=') return CARET_EQUAL;
            return ERROR;
        }
    },
    LESS_LESS(true) {
        @Override
        public Symbol next(char c) {
            if (c == '=') return LESS_LESS_EQUAL;
            return ERROR;
        }
    },
    GREATER_GREATER(true) {
        @Override
        public Symbol next(char c) {
            if (c == '=') return GREATER_GREATER_EQUAL;
            return ERROR;
        }
    },
    LESS(true) {
        @Override
        public Symbol next(char c) {
            switch (c) {
                case '=':   return LESS_EQUAL;
                case '<':   return LESS_LESS;
                default:    return ERROR;
            }
        }
    },
    GREATER(true) {
        @Override
        public Symbol next(char c) {
            switch (c) {
                case '=':   return GREATER_EQUAL;
                case '>':   return GREATER_GREATER;
                default:    return ERROR;
            }
        }
    },
    LESS_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    GREATER_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    EQUAL_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    BANG_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    EQUAL(true) {
        @Override
        public Symbol next(char c) {
            if (c == '=') return EQUAL_EQUAL;
            return ERROR;
        }
    },
    PLUS_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    MINUS_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    STAR_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    SLASH_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    SLASH_SLASH(false) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    PERCENT_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    AND_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    PIPE_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    CARET_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    LESS_LESS_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    GREATER_GREATER_EQUAL(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    LEFT_PARENTHESIS(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    RIGHT_PARENTHESIS(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    LEFT_BRACKET(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    RIGHT_BRACKET(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    LEFT_BRACE(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    RIGHT_BRACE(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    PERIOD(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    COMMA(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    SEMICOLON(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    COLON(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    END(true) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    },
    ERROR(false) {
        @Override
        public Symbol next(char c) {
            return ERROR;
        }
    };

    private final boolean accept;
    public boolean accept() {
        return this.accept;
    }

    Symbol(boolean accept) {
        this.accept = accept;
    }

    // HELPER METHODS

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    // --
}
