public abstract class Arithmetic extends Binary {
    public Arithmetic(Kind kind, Token token, Node left, Node right) {
        super(kind, token, left, right);
    }

    public static class Add extends Arithmetic {
        public Add(Token token, Node left, Node right) {
            super(Symbol.PLUS, token, left, right);
        }
    }

    public static class Subtract extends Arithmetic {
        public Subtract(Token token, Node left, Node right) {
            super(Symbol.MINUS, token, left, right);
        }
    }

    public static class Multiply extends Arithmetic {
        public Multiply(Token token, Node left, Node right) {
            super(Symbol.STAR, token, left, right);
        }
    }

    public static class Divide extends Arithmetic {
        public Divide(Token token, Node left, Node right) {
            super(Symbol.SLASH, token, left, right);
        }
    }

    public static class Mod extends Arithmetic {
        public Mod(Token token, Node left, Node right) {
            super(Symbol.PERCENT, token, left, right);
        }
    }
}
