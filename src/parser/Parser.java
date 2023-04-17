package parser;

import java.util.ArrayList;
import lexer.*;

public class Parser {
    private Lexer lexer;
    private Token current;

    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String WHITE = "\u001B[37m";

    public static void main(String[] args) {
        String[] files = new String[]{"Simple.txt", "null.txt", "Echo.txt", "Fibonacci.txt"};
        for (String file : files) {
            System.out.println(GREEN + file + RESET);
            Parser parser = new Parser("src/Test Programs/" + file);

            do {
                Node node = Constructs.PROGRAM.parse(parser);
//                System.out.println(node.token);
//                System.out.println(node);
                System.out.println(node.indentedTree(1));
            } while (parser.lexer.hasNextToken());
        }
    }

    public Parser() {
        this.lexer = new Lexer();
        this.current = (lexer.hasNextToken()) ? lexer.nextToken() : new Token(1, 1, Symbol.END);
    }

    public Parser(String filename) {
        this.lexer = new Lexer(filename);
        this.current = (lexer.hasNextToken()) ? lexer.nextToken() : new Token(1, 1, Symbol.END);
    }

    public String getFilename() {
        return this.lexer.getFilename();
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

    public enum Constructs {
        PROGRAM {
            @Override
            protected Program parse(Parser p) {
                Token token = p.advance(Keyword.PROGRAM);
                Identifier id = (Identifier) p.advance(Value.Type.IDENTIFIER);
                Block block = (Block) BLOCK.parse(p);
                return new Program(token, id, block);
            }
        },
        BLOCK {
            @Override
            protected Block parse(Parser p) {
                Block block = new Block(p.advance(Symbol.LEFT_BRACE)); //(Statement) STATEMENT.parse(p));
                Statement next = (Statement) STATEMENT.check(p);
                while (next != null) {
                    block.add(next);
                    next = (Statement) STATEMENT.check(p);
                }

                p.advance(Symbol.RIGHT_BRACE);
                return block;
            }
        },
        STATEMENT {
            @Override
            protected Statement parse(Parser p) {
//                Constructs[] statementTypes = new Constructs[]{DECLARATION, ASSIGNMENT, IF_STATEMENT, WHILE_STATEMENT, RETURN_STATEMENT, CALL_STATEMENT, BLOCK};
//                Statement statement;

                if (p.current.kind() == Symbol.LEFT_BRACE) {
                    return (Block) BLOCK.parse(p);
                }
                if (p.current.kind() == Value.Type.IDENTIFIER) return (Statement) ASSIGNMENT_OR_CALL.parse(p);

                if (p.current.kind() instanceof Keyword) {
                    switch ((Keyword) p.current.kind()) {
                        case IF -> { return (If) IF_STATEMENT.parse(p); }
                        case WHILE -> { return (While) WHILE_STATEMENT.parse(p); }
                        case RETURN -> { return (Return) RETURN_STATEMENT.parse(p); }
                        case INT, CHAR, BOOLEAN, VOID -> { return (Statement) DECLARATION.parse(p); }
                    }
                }

//                for (Constructs construct : statementTypes) {
//                    statement = (Statement) construct.check(p);
//                    if (statement != null) return statement;
//                }
//                throw new ExpectingException(this, p.current);

                throw new ExpectingException(this, p.current);
            }
        },
        ASSIGNMENT_OR_CALL {
            @Override
            protected Statement parse(Parser p) {
                Node name = VARIABLE.parse(p);
                Node[] prerequisites = new Node[]{name};

                if (name instanceof Index || p.check(p.ASSIGN_OPERATORS)) return (Assignment) ASSIGNMENT.parse(p, prerequisites);
                if (p.current.kind() == Symbol.LEFT_PARENTHESIS) return (Call) CALL_STATEMENT.parse(p, prerequisites);

                throw new ExpectingException(this, p.current);
            }
        },
        ASSIGNMENT {
            @Override
            protected Node parse(Parser p) {
                Variable variable = (Variable) VARIABLE.parse(p);
                return this.parse(p, new Node[]{variable});

//                Token operator = p.advance(p.ASSIGN_OPERATORS, "assign operator");
//                Node expression = EXPRESSION.parse(p);
//                p.advance(Symbol.SEMICOLON);
//                return operator.toNode(variable, expression);
            }

            @Override
            protected Node parse(Parser p, Node[] prerequisites) {
                if (!(prerequisites[0] instanceof Variable))
                    throw new ExpectingException(this, prerequisites[0].token);

                Token operator = p.advance(p.ASSIGN_OPERATORS, "assign operator");
                Node expression = EXPRESSION.parse(p);
                p.advance(Symbol.SEMICOLON);
                return operator.toNode((Variable) prerequisites[0], expression);
            }
        },
        CALL_STATEMENT {
            @Override
            protected Call parse(Parser p) {
                Call call = (Call) CALL.parse(p);
                p.advance(Symbol.SEMICOLON);
                return call;
            }

            @Override
            protected Call parse(Parser p, Node[] prerequisites) {
                Call call = (Call) CALL.parse(p, prerequisites);
                p.advance(Symbol.SEMICOLON);
                return call;
            }
        },
        CALL {
            @Override
            protected Call parse(Parser p) {
                Variable name = (Variable) NAME.parse(p);
                return this.parse(p, new Node[]{name});

//                p.advance(Symbol.LEFT_PARENTHESIS);
//                Arguments arguments = (Arguments) ARGUMENTS.check(p);
//                p.advance(Symbol.RIGHT_PARENTHESIS);
//                return new Call(name, arguments);
            }

            @Override
            protected Call parse(Parser p, Node[] prerequisites) {
                if (!(prerequisites[0] instanceof Variable))
                    throw new ExpectingException(this, prerequisites[0].token);

                p.advance(Symbol.LEFT_PARENTHESIS);
                Arguments arguments = (Arguments) ARGUMENTS.check(p);
                p.advance(Symbol.RIGHT_PARENTHESIS);
                return new Call((Variable) prerequisites[0], arguments);
            }
        },
        ARGUMENTS {
            @Override
            protected Arguments parse(Parser p) {
                Arguments result = new Arguments(EXPRESSION.parse(p));
//                while (p.check(Symbol.COMMA) != null) {
                while (p.check(Symbol.COMMA)) {
                    p.advance();
                    result.add(EXPRESSION.parse(p));
                }
                return result;
            }
        },
        IF_STATEMENT {
            @Override
            protected If parse(Parser p) {
                Token token = p.advance(Keyword.IF);
                p.advance(Symbol.LEFT_PARENTHESIS);
                Node condition = BOOLEAN_EXPRESSION.parse(p);
                p.advance(Symbol.RIGHT_PARENTHESIS);
                Statement primary = (Statement) STATEMENT.parse(p);

                Statement secondary = null;
//                Token elseToken = p.check(Keyword.ELSE);
                if (p.check(Keyword.ELSE)) {
                    p.advance();
                    secondary = (Statement) STATEMENT.parse(p);
                }

                return new If(token, condition, primary, secondary);
            }
        },
        WHILE_STATEMENT {
            @Override
            protected While parse(Parser p) {
                Token token = p.advance(Keyword.WHILE);
                p.advance(Symbol.LEFT_PARENTHESIS);
                Node condition = BOOLEAN_EXPRESSION.parse(p);
                p.advance(Symbol.RIGHT_PARENTHESIS);
                Statement statement = (Statement) STATEMENT.parse(p);

                return new While(token, condition, statement);
            }
        },
        RETURN_STATEMENT {
            @Override
            protected Return parse(Parser p) {
                Token token = p.advance(Keyword.RETURN);
                Node expression = EXPRESSION.check(p);
                p.advance(Symbol.SEMICOLON);

                return new Return(token, expression);
            }
        },
        DECLARATION {
            @Override
            protected Node parse(Parser p) {
//                Constructs[] declarationTypes = new Constructs[]{VARIABLE_DECLARATION, METHOD_DECLARATION};
//                Node declaration;
//                for (Constructs construct : declarationTypes) {
//                    declaration = construct.check(p);
//                    if (declaration != null) {
//                        p.advance(Symbol.SEMICOLON);
//                        return declaration;
//                    }
//                }

                Type type = new Type(p.advance(p.SCALAR_TYPES, "scalar type"));
                Identifier identifier = (Identifier) p.advance(Value.Type.IDENTIFIER);
                Node[] prerequisites = new Node[]{type, new Node(identifier)};

                Node declaration;
                if (p.check(Symbol.LEFT_PARENTHESIS)) return METHOD_DECLARATION.parse(p, prerequisites); //declaration = METHOD_DECLARATION.parse(p, prerequisites);
                else return VARIABLE_DECLARATION.parse(p, prerequisites); //declaration = VARIABLE_DECLARATION.parse(p, prerequisites);

//                p.advance(Symbol.SEMICOLON);
//                return declaration;
            }
        },
        VARIABLE_DECLARATION {
            @Override
            protected Node parse(Parser p) {
                Type type = new Type(p.advance(p.SCALAR_TYPES, "scalar type"));
                Identifier identifier = (Identifier) p.advance(Value.Type.IDENTIFIER);
                return this.parse(p, new Node[]{type, new Node(identifier)});

//                Type type = (Type) VARIABLE_TYPE.parse(p);
//                Type type = new Type(p.advance(p.SCALAR_TYPES, "scalar type"));
//                Identifier identifier = (Identifier) p.advance(Value.Type.IDENTIFIER);
//
//                VariableDeclaration declaration = new VariableDeclaration(type, identifier);
//
////                Token token = p.check(Symbol.EQUAL);
//                if (p.check(Symbol.EQUAL)) {
//                    Token token = p.advance();
//                    Node expression = EXPRESSION.parse(p);
//                    return new Assignment.Assign(token, declaration, expression);
//                }
//
//                return declaration;
            }

            @Override
            protected Node parse(Parser p, Node[] prerequisites) {
                if (!(prerequisites.length == 2 &&
                        prerequisites[0] instanceof Type &&
                        prerequisites[1].token.kind() == Value.Type.IDENTIFIER
                )) throw new ExpectingException(this, p.current);

                Type type = (Type) prerequisites[0];
                Identifier identifier = (Identifier) prerequisites[1].token;

                VariableDeclaration declaration = new VariableDeclaration(type, identifier);

//                Token token = p.check(Symbol.EQUAL);
                if (p.check(Symbol.EQUAL)) {
                    Token token = p.advance();
                    Node expression = EXPRESSION.parse(p);
                    p.advance(Symbol.SEMICOLON);
                    return new Assignment.Assign(token, declaration, expression);
                }

                p.advance(Symbol.SEMICOLON);
                return declaration;
            }
        },
        METHOD_DECLARATION {
            @Override
            protected MethodDeclaration parse(Parser p) {
//                Type type = new Type(p.scalarType(true));
                Type type = new Type(p.advance(p.SCALAR_TYPES, "scalar type"));
                Identifier identifier = (Identifier) p.advance(Value.Type.IDENTIFIER);
                return this.parse(p, new Node[]{type, new Node(identifier)});

//                p.advance(Symbol.LEFT_PARENTHESIS);
//                Parameters parameters = (Parameters) PARAMETERS.check(p);
//                p.advance(Symbol.RIGHT_PARENTHESIS);
//                Block block = (Block) BLOCK.parse(p);
//
//                return new MethodDeclaration(type, identifier, parameters, block);
            }

            @Override
            protected MethodDeclaration parse(Parser p, Node[] prerequisites) {
                if (!(  prerequisites.length == 2 &&
                        prerequisites[0] instanceof Type &&
                        prerequisites[1].token.kind() == Value.Type.IDENTIFIER
                )) throw new ExpectingException(this, p.current);

                Type type = (Type) prerequisites[0];
                Identifier identifier = (Identifier) prerequisites[1].token;

                p.advance(Symbol.LEFT_PARENTHESIS);
                Parameters parameters = (Parameters) PARAMETERS.check(p);
                p.advance(Symbol.RIGHT_PARENTHESIS);
                Block block = (Block) BLOCK.parse(p);

                return new MethodDeclaration(type, identifier, parameters, block);
            }
        },
        PARAMETERS {
            @Override
            protected Parameters parse(Parser p) {
                Parameters result = new Parameters((Parameter) PARAMETER.parse(p));
                while (p.check(Symbol.COMMA)) {
                    p.advance();
                    result.add((Parameter) PARAMETER.parse(p));
                }
                return result;
            }
        },
        PARAMETER {
            @Override
            protected Parameter parse(Parser p) {
                Type type = (Type) PARAMETER_TYPE.parse(p);
                Identifier identifier = (Identifier) p.advance(Value.Type.IDENTIFIER);
                return new Parameter(type, identifier);
            }
        },
        VARIABLE_TYPE {
            @Override
            protected Type parse(Parser p) {
//                Token type = p.scalarType(true);
                Token type = p.advance(p.SCALAR_TYPES, "scalar type");
//                if (p.check(Symbol.LEFT_BRACKET) != null) {
                if (p.check(Symbol.LEFT_BRACKET)) {
                    p.advance();
                    Node constant = CONSTANT_EXPRESSION.parse(p);
                    p.advance(Symbol.RIGHT_BRACKET);
                    return new Type(type, constant);
                }
                return new Type(type);
            }
        },
        PARAMETER_TYPE {
            @Override
            protected Type parse(Parser p) {
//                Token type = p.scalarType(true);
                Token type = p.advance(p.SCALAR_TYPES, "scalar type");
//                if (p.check(Symbol.LEFT_BRACKET) != null) {
                if (p.check(Symbol.LEFT_BRACKET)) {
                    p.advance();
                    p.advance(Symbol.RIGHT_BRACKET);
                    return new Type(type, true);
                }
                return new Type(type, false);
            }
        },
        BOOLEAN_EXPRESSION {
            @Override
            protected Node parse(Parser p) {
                return EXPRESSION.parse(p);
            }
        },
        CONSTANT_EXPRESSION {
            @Override
            protected Node parse(Parser p) {
                return EXPRESSION.parse(p);
            }
        },
        EXPRESSION {
            @Override
            protected Node parse(Parser p) {
                Node result = DISJUNCTION.parse(p);
//                Token operator = p.check(Symbol.EQUAL);
                while (p.check(Symbol.EQUAL)) {
                    Token operator = p.advance();
                    result = new Assignment.Assign(operator, (Variable) result, DISJUNCTION.parse(p)); // TODO
//                    operator = p.check(Symbol.EQUAL);
                }
                return result;
            }
        },
        DISJUNCTION {
            @Override
            protected Node parse(Parser p) {
                Node result = CONJUNCTION.parse(p);
//                Token operator = p.orOperator(false);
                while (p.check(p.OR_OPERATORS)) {
                    Token operator = p.advance();
                    result = operator.toNode(result, CONJUNCTION.parse(p));
//                    operator = p.orOperator(false);
                }
                return result;
            }
        },
        CONJUNCTION {
            @Override
            protected Node parse(Parser p) {
                Node result = RELATION.parse(p);
//                Token operator = p.andOperator(false);
                while (p.check(p.ADD_OPERATORS)) {
                    Token operator = p.advance();
                    result = operator.toNode(result, RELATION.parse(p));
//                    operator = p.andOperator(false);
                }
                return result;
            }
        },
        RELATION {
            @Override
            protected Node parse(Parser p) {
                Node result = SIMPLE_EXPRESSION.parse(p);
//                Token operator = p.compareOperator(false);
                while (p.check(p.COMPARE_OPERATORS)) {
                    Token operator = p.advance();
                    result = operator.toNode(result, SIMPLE_EXPRESSION.parse(p));
//                    operator = p.compareOperator(false);
                }
                return result;
            }
        },
        SIMPLE_EXPRESSION {
            @Override
            protected Node parse(Parser p) {
                Node result = TERM.parse(p);
//                Token operator = p.addOperator(false);
                while (p.check(p.ADD_OPERATORS)) {
                    Token operator = p.advance();
                    result = operator.toNode(result, TERM.parse(p));
//                    operator = p.addOperator(false);
                }
                return result;
            }
        },
        TERM {
            @Override
            protected Node parse(Parser p) {
                Node result = FACTOR.parse(p);
//                Token operator = p.multiplyOperator(false);
                while (p.check(p.MULTIPLY_OPERATORS)) {
                    Token operator = p.advance();
                    result = operator.toNode(result, FACTOR.parse(p));
//                    operator = p.multiplyOperator(false);
                }
                return result;
            }
        },
        FACTOR {
            @Override
            protected Node parse(Parser p) {
                Node primary = PRIMARY.check(p); //parse(p);
                if (primary != null) return primary;

//                Variable name = (Variable) NAME.check(p); //parse(p);
//                if (name != null) {
//                    Node index = FACTOR_INDEX.check(p);
//                    if (index != null) return new Index(name, index);
//
//                    Arguments arguments = (Arguments) FACTOR_ARGUMENTS.check(p);
//                    if (arguments != null) return new Call(name, arguments);
//
//                    return name;
//                }

//                Token operator = p.unaryOperator(false);
//                if (operator != null) {
                if (p.check(p.UNARY_OPERATORS)) {
                    Token operator = p.advance();
                    Node factor = FACTOR.parse(p);
                    return operator.toNode(factor);
                }

//                operator = p.prefixOperator(false);
//                if (operator != null) {
                if (p.check(p.PREFIX_OPERATORS)) {
                    Token operator = p.advance();
                    Node variable = VARIABLE.parse(p);
                    return operator.toNode(variable, true);
                }

                throw new ExpectingException("factor", p.current);
            }
        },
        FACTOR_INDEX {
            @Override
            protected Node parse(Parser p) {
                p.advance(Symbol.LEFT_BRACKET);
                Node result = EXPRESSION.parse(p);
                p.advance(Symbol.RIGHT_BRACKET);
//                Token postfix = p.postfixOperator(false);
//                return (postfix == null) ? result : postfix.toNode(result);
                if (!p.check(p.POSTFIX_OPERATORS)) return result;
                else {
                    Token postfix = p.advance();
                    return postfix.toNode(result);
                }
            }
        },
        FACTOR_ARGUMENTS {
            @Override
            protected Node parse(Parser p) {
                p.advance(Symbol.LEFT_PARENTHESIS);
                Arguments result = (Arguments) ARGUMENTS.parse(p);
                p.advance(Symbol.RIGHT_PARENTHESIS);
                return result;
            }
        },
        PRIMARY {
            @Override
            protected Node parse(Parser p) {
                Constructs[] primaryTypes = new Constructs[]{CALL, VARIABLE};

//                Node primary;
//                for (Constructs construct : primaryTypes) {
//                    primary = construct.check(p);
//                    if (primary != null) return primary;
//                }

                // TODO
                if (p.check(Value.Type.IDENTIFIER)) {
                    Node name = VARIABLE.parse(p);
                    Node[] prerequisites = new Node[]{name};

//                    if (name instanceof Index) return name;
                    if (p.current.kind() == Symbol.LEFT_PARENTHESIS) return (Call) CALL.parse(p, prerequisites); // CALL_STATEMENT
                    if (p.check(p.POSTFIX_OPERATORS)) {
                        Token operator = p.advance();
                        return operator.toNode(name, false);
                    }

                    return name;
                }

                if (p.check(p.LITERALS)) {
                    Value value = (Value) p.advance(p.LITERALS, "literal");
                    return new Literal(value);
                }

                if (p.check(Symbol.LEFT_PARENTHESIS)) {
                    p.advance();
                    Node result = EXPRESSION.parse(p);
                    p.advance(Symbol.RIGHT_PARENTHESIS);
                    return result;
                }

                throw new ExpectingException(this, p.current);
            }
        },
        VARIABLE {
            @Override
            protected Node parse(Parser p) {
                Variable variable = (Variable) NAME.parse(p);
                if (p.check(Symbol.LEFT_BRACKET)) {
                    p.advance();
                    Node index = EXPRESSION.parse(p);
                    p.advance(Symbol.RIGHT_BRACKET);
                    return new Index(variable, index);
                }
                return variable;
            }
        },
        NAME {
            protected Variable parse(Parser p) {
                Identifier name = (Identifier) p.advance(Value.Type.IDENTIFIER);
                ArrayList<Identifier> path = new ArrayList<>(3);
                while (p.check(Symbol.PERIOD)) {
                    p.advance();
                    path.add(name);
                    name = (Identifier) p.advance(Value.Type.IDENTIFIER);
                }
                return (path.isEmpty()) ? new Variable(name) : new Variable(name, (Identifier[]) path.toArray());
            }
        };

        protected abstract Node parse(Parser p);
        protected Node parse(Parser p, Node[] prerequisites) {
            return this.parse(p);
        }
        protected Node check(Parser p) {
            int line = p.current.line();
            int column = p.current.column();

            try {
                return this.parse(p);
            } catch (ExpectingException e) {
                return null;
            }
        }
    }


//    public Node program(boolean required) {
//        try {
//            Token program = advance(Keyword.PROGRAM);
//            Identifier id = (Identifier) advance(Value.Type.IDENTIFIER);
//            Node block = block(true);
//            return program.toNode(id, block);
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node block(boolean required) {
//        try {
//            advance(Symbol.LEFT_BRACE);
//            Node statements = statements(true);
//            advance(Symbol.RIGHT_BRACE);
//            return new Statements(statements);
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node statements(boolean required) {
//        return null;
//    }
//
//    private Node expression(boolean required) {
//        try {
//            Node result = disjunction(true);
//            Token operator = check(Symbol.EQUAL);
//            while (operator != null) {
//                result = operator.toNode(result, disjunction(true));
//                operator = check(Symbol.EQUAL);
//            }
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node disjunction(boolean required) {
//        try {
//            Node result = conjunction(true);
//            Token operator = orOperator(false);
//            while (operator != null) {
//                result = operator.toNode(result, conjunction(true));
//                operator = orOperator(false);
//            }
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node conjunction(boolean required) {
//        try {
//            Node result = relation(true);
//            Token operator = andOperator(false);
//            while (operator != null) {
//                result = operator.toNode(result, relation(true));
//                operator = andOperator(false);
//            }
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node relation(boolean required) {
//        try {
//            Node result = simpleExpression(true);
//            Token operator = compareOperator(false);
//            while (operator != null) {
//                result = operator.toNode(result, simpleExpression(true));
//                operator = compareOperator(false);
//            }
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node simpleExpression(boolean required) {
//        try {
//            Token sign = sign(false);
//            Node result = term(true);
//            Token operator = addOperator(false);
//            while (operator != null) {
//                result = operator.toNode(result, term(true));
//                operator = addOperator(false);
//            }
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node term(boolean required) {
//        try {
//            Node result = factor(true);
//            Token operator = multiplyOperator(false);
//            while (operator != null) {
//                result = operator.toNode(result, factor(true));
//                operator = multiplyOperator(false);
//            }
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node factor(boolean required) {
//        Variable name = name(false);
//        if (name != null) {
//            Node index = factorIndex(false);
//            if (index != null) return new Index(name, index);
//
//            Arguments arguments = factorArguments(false);
//            if (arguments != null) return new Call(name, arguments);
//
//            return name;
//        }
//
//        Value value = literal(false);
//        if (value != null) return new Literal(value);
//
//        Token operator = unaryOperator(false);
//        if (operator != null) {
//            Node factor = factor(true);
//            return operator.toNode(factor);
//        }
//
//        operator = prefixOperator(false);
//        if (operator != null) {
//            Node variable = variable(true);
//            return operator.toNode(variable, true);
//        }
//
//        if (check(Symbol.LEFT_PARENTHESIS) != null) {
//            Node result = expression(true);
//            advance(Symbol.RIGHT_PARENTHESIS);
//            return result;
//        }
//
//        if (required) throw new ExpectingException("factor", current);
//        return null;
//    }
//
//    private Variable name(boolean required) {
//        try {
//            Identifier name = (Identifier) advance(Value.Type.IDENTIFIER);
//            ArrayList<Identifier> path = new ArrayList<>(3);
//            while (check(Symbol.PERIOD) != null) {
//                path.add(name);
//                name = (Identifier) advance(Value.Type.IDENTIFIER);
//            }
//            return (path.isEmpty()) ? new Variable(name) : new Variable(name, (Identifier[]) path.toArray());
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node variable(boolean required) {
//        try {
//            Variable variable = name(true);
//            if (check(Symbol.LEFT_BRACKET) != null) {
//                Node index = expression(true);
//                advance(Symbol.RIGHT_BRACKET);
//                return new Index(variable, index);
//            }
//            return variable;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Node factorIndex(boolean required) {
//        try {
//            advance(Symbol.LEFT_BRACKET);
//            Node result = expression(true);
//            advance(Symbol.RIGHT_BRACKET);
//            postfixOperator(false);
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Arguments factorArguments(boolean required) {
//        try {
//            advance(Symbol.LEFT_PARENTHESIS);
//            Arguments result = arguments(true);
//            advance(Symbol.RIGHT_PARENTHESIS);
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }
//
//    private Arguments arguments(boolean required) {
//        try {
//            Arguments result = new Arguments(expression(true));
//            while (check(Symbol.COMMA) != null) {
//                result.add(expression(true));
//            }
//            return result;
//        } catch (ExpectingException e) {
//            if (required) throw e;
//            return null;
//        }
//    }

    private final Keyword[] SCALAR_TYPES = new Keyword[]{Keyword.BOOLEAN, Keyword.CHAR, Keyword.INT, Keyword.VOID};
    private final Symbol[] ASSIGN_OPERATORS = new Symbol[]{Symbol.EQUAL, Symbol.PLUS_EQUAL, Symbol.MINUS_EQUAL, Symbol.STAR_EQUAL, Symbol.SLASH_EQUAL, Symbol.PERCENT_EQUAL, Symbol.AND_EQUAL, Symbol.PIPE_EQUAL, Symbol.CARET_EQUAL};
    private final Symbol[] OR_OPERATORS = new Symbol[]{Symbol.PIPE, Symbol.PIPE_PIPE, Symbol.CARET};
    private final Symbol[] AND_OPERATORS = new Symbol[]{Symbol.AND, Symbol.AND_AND};
    private final Symbol[] COMPARE_OPERATORS = new Symbol[]{Symbol.LESS, Symbol.LESS_EQUAL, Symbol.GREATER, Symbol.GREATER_EQUAL, Symbol.EQUAL_EQUAL, Symbol.BANG_EQUAL};
    private final Symbol[] UNARY_OPERATORS = new Symbol[]{Symbol.BANG};
    private final Symbol[] SIGNS = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
    private final Symbol[] ADD_OPERATORS = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
    private final Symbol[] MULTIPLY_OPERATORS = new Symbol[]{Symbol.STAR, Symbol.SLASH, Symbol.PERCENT};
    private final Value.Type[] LITERALS = new Value.Type[]{Value.Type.INTEGER, Value.Type.CHARACTER, Value.Type.STRING};
    private final Symbol[] PREFIX_OPERATORS = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};
    private final Symbol[] POSTFIX_OPERATORS = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};

//    private Token scalarType(boolean required) {
//        Keyword[] keywords = new Keyword[]{Keyword.BOOLEAN, Keyword.CHAR, Keyword.INT, Keyword.VOID};
//        return (required) ? advance(keywords, "scalar type") : check(keywords);
//    }
//
//    private Token assignOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.EQUAL, Symbol.PLUS_EQUAL, Symbol.MINUS_EQUAL, Symbol.STAR_EQUAL, Symbol.SLASH_EQUAL, Symbol.PERCENT_EQUAL, Symbol.AND_EQUAL, Symbol.PIPE_EQUAL, Symbol.CARET_EQUAL};
//        return (required) ? advance(symbols, "assign operator") : check(symbols);
//    }
//
//    private Token orOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.PIPE, Symbol.PIPE_PIPE, Symbol.CARET};
//        return (required) ? advance(symbols, "or operator") : check(symbols);
//    }
//
//    private Token andOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.AND, Symbol.AND_AND};
//        return (required) ? advance(symbols, "and operator") : check(symbols);
//    }
//
//    private Token compareOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.LESS, Symbol.LESS_EQUAL, Symbol.GREATER, Symbol.GREATER_EQUAL, Symbol.EQUAL_EQUAL, Symbol.BANG_EQUAL};
//        return (required) ? advance(symbols, "compare operator") : check(symbols);
//    }
//
//    private Token unaryOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.BANG};
//        return (required) ? advance(symbols, "unary operator") : check(symbols);
//    }
//
//    private Token sign(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
//        return (required) ? advance(symbols, "sign") : check(symbols);
//    }
//
//    private Token addOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.PLUS, Symbol.MINUS};
//        return (required) ? advance(symbols, "add operator") : check(symbols);
//    }
//
//    private Token multiplyOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.STAR, Symbol.SLASH, Symbol.PERCENT};
//        return (required) ? advance(symbols, "multiply operator") : check(symbols);
//    }
//
//    private Value literal(boolean required) {
//        Kind[] tokens = new Kind[]{Value.Type.INTEGER, Value.Type.CHARACTER, Value.Type.STRING};
//        return (Value) ((required) ? advance(tokens, "literal") : check(tokens));
//    }
//
//    private Token prefixOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};
//        return (required) ? advance(symbols, "prefix operator") : check(symbols);
//    }
//
//    private Token postfixOperator(boolean required) {
//        Symbol[] symbols = new Symbol[]{Symbol.PLUS_PLUS, Symbol.MINUS_MINUS};
//        return (required) ? advance(symbols, "postfix operator") : check(symbols);
//    }

//    private Token check(Kind[] kinds) {
//        Kind kind = current.kind();
//        for (Kind k : kinds) {
//            if (kind == k) {
//                Token t = current;
//                advance();
//                return t;
//            }
//        }
//        return null;
//    }
//
//    private Token check(Kind kind) {
//        if (current.kind() == kind) {
//            Token t = current;
//            advance();
//            return t;
//        }
//        return null;
//    }

    private boolean check(Kind[] kinds) {
        Kind kind = current.kind();
        for (Kind k : kinds) {
            if (kind == k) return true;
        }
        return false;
    }

    private boolean check(Kind kind) {
        return current.kind() == kind;
    }

    private Token advance(Kind[] kinds, String name) {
//        Token result = check(kinds);
//        if (result != null) return result;

        boolean result = check(kinds);
        if (result) {
            Token t = current;
            advance();
            return t;
        }
        throw new ExpectingException(name, current);
    }

    private Token advance(Kind kind) {
        String name = kind.name().toLowerCase().replace('_', ' ');
//        Token result = check(kind);
//        if (result != null) return result;

        boolean result = check(kind);
        if (result) {
            Token t = current;
            advance();
            return t;
        }
        throw new ExpectingException(name, current);
    }
}
