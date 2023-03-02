public class Assignment extends Statement {
    Node variable;
    Node expression;
    public Assignment(Kind kind, Token token, Node variable, Node expression) {
        super(token);

        if (token.kind != kind) throw new ExpectingException(kind, token);

        this.variable = variable;
        this.expression = expression;
    }

    public static class Assign extends Assignment {
        public Assign(Token token, Node variable, Node expression) {
            super(Symbol.EQUAL, token, variable, expression);
        }
    }

    public static class Add extends Assignment {
        public Add(Token token, Variable variable, Node expression) {
            super(Symbol.PLUS, token, variable, expression);
        }
    }

    public static class Subtract extends Assignment {
        public Subtract(Token token, Variable variable, Node expression) {
            super(Symbol.MINUS, token, variable, expression);
        }
    }

    public static class Multiply extends Assignment {
        public Multiply(Token token, Variable variable, Node expression) {
            super(Symbol.STAR, token, variable, expression);
        }
    }

    public static class Divide extends Assignment {
        public Divide(Token token, Variable variable, Node expression) {
            super(Symbol.SLASH, token, variable, expression);
        }
    }

    public static class Mod extends Assignment {
        public Mod(Token token, Variable variable, Node expression) {
            super(Symbol.PERCENT, token, variable, expression);
        }
    }

    public static class And extends Assignment {
        public And(Token token, Variable variable, Node expression) {
            super(Symbol.AND, token, variable, expression);
        }
    }

    public static class Or extends Assignment {
        public Or(Token token, Variable variable, Node expression) {
            super(Symbol.PIPE, token, variable, expression);
        }
    }

    public static class Xor extends Assignment {
        public Xor(Token token, Variable variable, Node expression) {
            super(Symbol.CARET, token, variable, expression);
        }
    }
}
