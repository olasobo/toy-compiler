public class Postfix extends Unary {
    private Postfix(Kind kind, Token token, Node child) {
        super(kind, token, child);
    }

    @Override
    public String toString() {
        return child.toString() + " " + token.kind;
    }

    public static class Increment extends Postfix {
        public Increment(Token token, Node child) {
            super(Symbol.PLUS_PLUS, token, child);
        }
    }

    public static class Decrement extends Postfix {
        public Decrement(Token token, Node child) {
            super(Symbol.MINUS_MINUS, token, child);
        }
    }
}
