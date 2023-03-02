public class Prefix extends Unary {
    private Prefix(Kind kind, Token token, Node child) {
        super(kind, token, child);
    }

    @Override
    public String toString() {
        return token.kind.name() + " " + child;
    }

    public static class Increment extends Prefix {
        public Increment(Token token, Node child) {
            super(Symbol.PLUS_PLUS, token, child);
        }
    }

    public static class Decrement extends Prefix {
        public Decrement(Token token, Node child) {
            super(Symbol.MINUS_MINUS, token, child);
        }
    }

    public static class Negation extends Prefix {
        public Negation(Token token, Node child) {
            super(Symbol.BANG, token, child);
        }
    }

    public static class Identity extends Prefix {
        public Identity(Token token, Node child) {
            super(Symbol.PLUS, token, child);
        }
    }

    public static class Negative extends Prefix {
        public Negative(Token token, Node child) {
            super(Symbol.MINUS, token, child);
        }
    }
}
