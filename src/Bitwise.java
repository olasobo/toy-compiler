public class Bitwise extends Binary {
    private Bitwise(Kind kind, Token token, Node left, Node right) {
        super(kind, token, left, right);
    }

    public static class And extends Bitwise {
        public And(Token token, Node left, Node right) {
            super(Symbol.AND, token, left, right);
        }
    }

    public static class Or extends Bitwise {
        public Or(Token token, Node left, Node right) {
            super(Symbol.PIPE, token, left, right);
        }
    }

    public static class Xor extends Bitwise {
        public Xor(Token token, Node left, Node right) {
            super(Symbol.CARET, token, left, right);
        }
    }

    public static class ShiftRight extends Bitwise {
        public ShiftRight(Token token, Node left, Node right) {
            super(Symbol.GREATER_GREATER, token, left, right);
        }
    }

    public static class ShiftLeft extends Bitwise {
        public ShiftLeft(Token token, Node left, Node right) {
            super(Symbol.LESS_LESS, token, left, right);
        }
    }
}
