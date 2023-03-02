public class Checker {
    private Lexer lexer;
    private Token current;

    public class ExpectingException extends RuntimeException {
        public ExpectingException(Kind expecting, Token found) {
            this(expecting.toString(), found);
        }

        public ExpectingException(String expecting, Token found) {
            super("Expecting " + expecting + ", found " + found.kind + " at " + found.line + ":" + found.column);
        }
    }

    public Checker() {
        this.lexer = new Lexer();
        this.current = (lexer.hasNextToken()) ? lexer.nextToken() : new Token(1, 1, Symbol.END);
    }

    public Checker(String filename) {
        this.lexer = new Lexer(filename);
        this.current = (lexer.hasNextToken()) ? lexer.nextToken() : new Token(1, 1, Symbol.END);
    }

    private void advance() {
        this.current = lexer.nextToken();
    }

    public boolean expression(boolean required) {
        disjunction(true);
        while (checkCurrent(Symbol.EQUAL, false)) {
            disjunction(true);
        }
        return true;
    }

    public boolean disjunction(boolean required) {
        conjunction(true);
        while (orOperation(false)) {
            conjunction(true);
        }
        return true;
    }

    public boolean conjunction(boolean required) {
        relation(true);
        while (andOperation(false)) {
            relation(true);
        }
        return true;
    }

    public boolean relation(boolean required) {
        simpleExpression(true);
        while (compareOperation(false)) {
            simpleExpression(true);
        }
        return true;
    }

    public boolean simpleExpression(boolean required) {
        sign(false);
        term(true);
        while (addOperation(false)) {
            term(true);
        }
        return true;
    }

    public boolean term(boolean required) {
        factor(true);
        while (multiplyOperator(false)) {
            factor(true);
        }
        return true;
    }

    public boolean factor(boolean required) {
        Kind kind = current.kind;
        if (name(false)) {
            return factorSuffix(false);
        } else if (checkCurrent(Symbol.BANG, false)) {
            return factor(true);
        } else if (prefixOperator(false)) {
            return variable(true);
        } else if (literal(false)) {
            return true;
        } else if (checkCurrent(Symbol.LEFT_PARENTHESIS, false)) {
            expression(true);
            return checkCurrent(Symbol.RIGHT_PARENTHESIS, true);
        }
        throw new ExpectingException("factor", current);
    }

    public boolean name(boolean required) {
        if (!checkCurrent(Value.Type.IDENTIFIER, required)) return false;
        while (checkCurrent(Symbol.PERIOD, false)) {
            checkCurrent(Value.Type.IDENTIFIER, true);
        }
        return true;
    }

    public boolean variable(boolean required) {
        name(true);
        if (checkCurrent(Symbol.LEFT_BRACKET, false)) {
            expression(true);
            checkCurrent(Symbol.RIGHT_BRACKET, true);
        }
        return true;
    }

    public boolean factorSuffix(boolean required) {
        if (checkCurrent(Symbol.LEFT_PARENTHESIS, false)) {
            arguments(false);
            checkCurrent(Symbol.RIGHT_PARENTHESIS, true);
        } else if (checkCurrent(Symbol.LEFT_BRACKET, false)) {
            expression(true);
            checkCurrent(Symbol.RIGHT_BRACKET, true);
            postfixOperator(false);
        }
        if (required)   throw new ExpectingException("factor suffix", current);
        else            return false;
    }

    public boolean arguments(boolean required) {
        expression(true);
        while (checkCurrent(Symbol.COMMA, false)) {
            expression(true);
        }
        return true;
    }

    public boolean orOperation(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PIPE, Symbol.PIPE_PIPE, Symbol.CARET};
        return checkCurrent(symbols, "or operation", required);
    }

    public boolean andOperation(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.AND, Symbol.AND_AND};
        return checkCurrent(symbols, "and operation", required);
    }

    public boolean compareOperation(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.LESS, Symbol.LESS_EQUAL, Symbol.GREATER, Symbol.GREATER_EQUAL, Symbol.EQUAL_EQUAL, Symbol.BANG_EQUAL};
        return checkCurrent(symbols, "compare operation", required);
    }

    public boolean sign(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
        return checkCurrent(symbols, "sign", required);
    }

    public boolean addOperation(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
        return checkCurrent(symbols, "add operation", required);
    }

    public boolean multiplyOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.STAR, Symbol.SLASH, Symbol.PERCENT};
        return checkCurrent(symbols, "multiply operator", required);
    }

    public boolean literal(boolean required) {
        Kind[] tokens = new Kind[]{Value.Type.INTEGER, Value.Type.CHARACTER, Value.Type.STRING};
        return checkCurrent(tokens, "literal", required);
    }

    public boolean prefixOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};
        return checkCurrent(symbols, "prefix operator", required);
    }

    public boolean postfixOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};
        return checkCurrent(symbols, "postfix operator", required);
    }

    private boolean checkCurrent(Kind[] kinds, String name, boolean required) {
        Kind kind = current.kind;
        for (Kind k : kinds) {
            if (kind == k) {
                this.advance();
                return true;
            }
        }
        if (required)  throw new ExpectingException(name, current);
        else        return false;
    }

    private boolean checkCurrent(Kind kind, boolean required) {
        String name = kind.name().toLowerCase().replace('_', ' ');
        return checkCurrent(new Kind[]{kind}, name, required);
    }

    public static void main(String[] args) {
        //Checker checker = new Checker();
        Checker checker = new Checker(args[0]);
        do {
            checker.expression(true);
            System.out.println("ok");
        } while (checker.lexer.hasNextToken());
    }
}
