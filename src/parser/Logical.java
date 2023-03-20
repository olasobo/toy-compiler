package parser;
import lexer.*;

public class Logical extends Binary {
    public Logical(Kind kind, Token token, Node left, Node right) {
        super(kind, token, left, right);
    }

    public static class And extends Logical {
        public And(Token token, Node left, Node right) {
            super(Symbol.AND_AND, token, left, right);
        }
    }

    public static class Or extends Logical {
        public Or(Token token, Node left, Node right) {
            super(Symbol.PIPE_PIPE, token, left, right);
        }
    }
}
