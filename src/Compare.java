public class Compare extends Binary {
    private Compare(Kind kind, Token token, Node left, Node right) {
        super(kind, token, left, right);
    }

    public static class LessThan extends Compare {
        public LessThan(Token token, Node left, Node right) {
            super(Symbol.LESS, token, left, right);
        }
    }

    public static class LessEqual extends Compare {
        public LessEqual(Token token, Node left, Node right) {
            super(Symbol.LESS_EQUAL, token, left, right);
        }
    }

    public static class GreaterThan extends Compare {
        public GreaterThan(Token token, Node left, Node right) {
            super(Symbol.GREATER, token, left, right);
        }
    }

    public static class GreaterEqual extends Compare {
        public GreaterEqual(Token token, Node left, Node right) {
            super(Symbol.GREATER_EQUAL, token, left, right);
        }
    }

    public static class Equal extends Compare {
        public Equal(Token token, Node left, Node right) {
            super(Symbol.EQUAL_EQUAL, token, left, right);
        }
    }

    public static class NotEqual extends Compare {
        public NotEqual(Token token, Node left, Node right) {
            super(Symbol.BANG_EQUAL, token, left, right);
        }
    }
}
