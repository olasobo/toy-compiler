import java.util.ArrayList;

public class ParserCopy {
    private Lexer lexer;
    private Token current;

    public ParserCopy() {
        this.lexer = new Lexer();
        this.current = (lexer.hasNextToken()) ? lexer.nextToken() : new Token(1, 1, Symbol.END);
    }

    public ParserCopy(String filename) {
        this.lexer = new Lexer(filename);
        this.current = (lexer.hasNextToken()) ? lexer.nextToken() : new Token(1, 1, Symbol.END);
    }

    private Token advance() {
        try {
            Token t = current;
            this.current = lexer.nextToken();
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    public Node expression(boolean required) {
        try {
            Node result = disjunction(true);
            Token operator = check(Symbol.EQUAL);
            while (operator != null) {
                result = operator.toNode(result, disjunction(true));
                operator = check(Symbol.EQUAL);
            }
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Node disjunction(boolean required) {
        try {
            Node result = conjunction(true);
            Token operator = orOperator(false);
            while (operator != null) {
                result = operator.toNode(result, conjunction(true));
                operator = orOperator(false);
            }
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Node conjunction(boolean required) {
        try {
            Node result = relation(true);
            Token operator = andOperator(false);
            while (operator != null) {
                result = operator.toNode(result, relation(true));
                operator = andOperator(false);
            }
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Node relation(boolean required) {
        try {
            Node result = simpleExpression(true);
            Token operator = compareOperator(false);
            while (operator != null) {
                result = operator.toNode(result, simpleExpression(true));
                operator = compareOperator(false);
            }
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Node simpleExpression(boolean required) {
        try {
            Token sign = sign(false);
            Node result = term(true);
            Token operator = addOperator(false);
            while (operator != null) {
                result = operator.toNode(result, term(true));
                operator = addOperator(false);
            }
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }

//        sign(false);
//        term(true);
//        while (addOperator(false)) {
//            term(true);
//        }
//        return true;
    }

    public Node term(boolean required) {
        try {
            Node result = factor(true);
            Token operator = multiplyOperator(false);
            while (operator != null) {
                result = operator.toNode(result, factor(true));
                operator = multiplyOperator(false);
            }
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Node factor(boolean required) {
        Variable name = name(false);
        if (name != null) {
            Node index = factorIndex(false);
            if (index != null) return new Index(name, index);

            Arguments arguments = factorArguments(false);
            if (arguments != null) return new Call(name, arguments);

            return name;
        }

        Value value = literal(false);
        if (value != null) return new Literal(value);

        Token operator = check(Symbol.BANG);
        if (operator != null) {
            Node factor = factor(true);
            return operator.toNode(factor);
        }

        operator = prefixOperator(false);
        if (operator != null) {
            Node variable = variable(true);
            return operator.toNode(variable, true);
        }

        if (check(Symbol.LEFT_PARENTHESIS) != null) {
            Node result = expression(true);
            advance(Symbol.RIGHT_PARENTHESIS);
            return result;
        }

        if (required) throw new ExpectingException("factor", current);
        return null;

//        try {
//            name(false);
//            return factorSuffix(false);
//        } catch (ExpectingException e) {}
//
//        if (checkCurrent(Symbol.BANG, false)) {
//            return factor(true);
//        } else if (prefixOperator(false)) {
//            return variable(true);
//        } else if (literal(false)) {
//            return true;
//        } else if (checkCurrent(Symbol.LEFT_PARENTHESIS, false)) {
//            expression(true);
//            return checkCurrent(Symbol.RIGHT_PARENTHESIS, true);
//        }
//        throw new ExpectingException("factor", current);
    }

    public Variable name(boolean required) {
        try {
            Identifier name = (Identifier) advance(Value.Kinds.IDENTIFIER);
            ArrayList<Identifier> path = new ArrayList<>(3);
            while (check(Symbol.PERIOD) != null) {
                path.add(name);
                name = (Identifier) advance(Value.Kinds.IDENTIFIER);
            }
            return (path.isEmpty()) ? new Variable(name) : new Variable(name, (Identifier[]) path.toArray());
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Node variable(boolean required) {
        try {
            Variable variable = name(true);
            if (check(Symbol.LEFT_BRACKET) != null) {
                Node index = expression(true);
                advance(Symbol.RIGHT_BRACKET);
                return new Index(variable, index);
            }
            return variable;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

//    public Node factorSuffix(boolean required) {
//        Node result;
//        if (current.kind instanceof Symbol) {
//            switch ((Symbol) current.kind) {
//                case LEFT_PARENTHESIS -> {
//                    result = arguments(false);
//                    advance(Symbol.RIGHT_PARENTHESIS);
//                    return result;
//                }
//                case LEFT_BRACKET -> {
//                    result = expression(true);
//                    advance(Symbol.RIGHT_BRACKET);
//                    postfixOperator(false);
//
//                }
//            }
//        }
//
//        if (check(Symbol.LEFT_PARENTHESIS)) {
//            result = arguments(false);
//            advance(Symbol.RIGHT_PARENTHESIS);
//            return
//        } else if (checkCurrent(Symbol.LEFT_BRACKET, false)) {
//            expression(true);
//            checkCurrent(Symbol.RIGHT_BRACKET, true);
//            postfixOperator(false);
//        }
//
//        if (required)   throw new ExpectingException("factor suffix", current);
//        else            return false;
//    }

    public Node factorIndex(boolean required) {
        try {
            advance(Symbol.LEFT_BRACKET);
            Node result = expression(true);
            advance(Symbol.RIGHT_BRACKET);
            postfixOperator(false);
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Arguments factorArguments(boolean required) {
        try {
            advance(Symbol.LEFT_PARENTHESIS);
            Arguments result = arguments(true);
            advance(Symbol.RIGHT_PARENTHESIS);
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Arguments arguments(boolean required) {
        try {
            Arguments result = new Arguments(expression(true));
            while (check(Symbol.COMMA) != null) {
                result.add(expression(true));
            }
            return result;
        } catch (ExpectingException e) {
            if (required) throw e;
            return null;
        }
    }

    public Token orOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PIPE, Symbol.PIPE_PIPE, Symbol.CARET};
        return (required) ? advance(symbols, "or operator") : check(symbols);
    }

    public Token andOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.AND, Symbol.AND_AND};
        return (required) ? advance(symbols, "and operator") : check(symbols);
    }

    public Token compareOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.LESS, Symbol.LESS_EQUAL, Symbol.GREATER, Symbol.GREATER_EQUAL, Symbol.EQUAL_EQUAL, Symbol.BANG_EQUAL};
        return (required) ? advance(symbols, "compare operator") : check(symbols);
    }

    public Token sign(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
        return (required) ? advance(symbols, "sign") : check(symbols);
    }

    public Token addOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
        return (required) ? advance(symbols, "add operator") : check(symbols);
    }

    public Token multiplyOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.STAR, Symbol.SLASH, Symbol.PERCENT};
        return (required) ? advance(symbols, "multiply operator") : check(symbols);
    }

    public Value literal(boolean required) {
        Kind[] tokens = new Kind[]{Value.Kinds.INTEGER, Value.Kinds.CHARACTER, Value.Kinds.STRING};
        return (Value) ((required) ? advance(tokens, "literal") : check(tokens));
    }

    public Token prefixOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};
        return (required) ? advance(symbols, "prefix operator") : check(symbols);
    }

    public Token postfixOperator(boolean required) {
        Symbol[] symbols = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};
        return (required) ? advance(symbols, "postfix operator") : check(symbols);
    }

    private Token check(Kind[] kinds) {
        Kind kind = current.kind;
        for (Kind k : kinds) {
            if (kind == k) {
                Token t = current;
                advance();
                return t;
            }
        }
        return null;
    }

    private Token check(Kind kind) {
        if (current.kind == kind) {
            Token t = current;
            advance();
            return t;
        }
        return null;
    }

    private Token advance(Kind[] kinds, String name) {
        Token result = check(kinds);
        if (result != null) return result;
        throw new ExpectingException(name, current);
    }

    private Token advance(Kind kind) {
        String name = kind.name().toLowerCase().replace('_', ' ');
        Token result = check(kind);
        if (result != null) return result;
        throw new ExpectingException(name, current);
    }

    public static void main(String[] args) {
        //Parser parser = new Parser();
        Parser parser = new Parser(args[0]);
        do {
            Node node = parser.expression(true);
            System.out.println(node.token);
            System.out.println(node);
        } while (parser.lexer.hasNextToken());
    }
}
