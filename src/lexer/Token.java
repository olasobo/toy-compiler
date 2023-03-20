package lexer;

import parser.*;

public class Token {
    final int TAB_SPACES = 8;
    int maxTabs = 1; // number of tabs

    protected int line;
    protected int column;
    protected Kind kind;

    public Token(int line, int column, Kind kind) {
        this.line = line;
        this.column = column;
        this.kind = kind;
    }

    public int line() {
        return this.line;
    }
    public int column() {
        return this.column;
    }
    public Kind kind() {
        return this.kind;
    }

    @Override
    public String toString() {
        return toString(kind.toString());
    }

    protected String toString(String name) {
        return this.toString(name, false);
    }

    protected String toString(String name, boolean position) {
        int tabs;
        do {
            maxTabs++;
            tabs = 1 + (int) Math.ceil(maxTabs - (double) name.length() / TAB_SPACES);
        } while (tabs < 1);
        return name + ((position) ? "\t".repeat(tabs) + " @ " + this.line + ":" + this.column : "");
    }

    public static Token parse(int line, int column, String s) {
        State state = Symbol.START;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            state = /*(TokenState)*/ state.next(c);

//            switch (state) {
//                case INTEGER:   return new Int(line, column, s);
//                case CHARACTER: return new Char(line, column, s);
//                case STRING:    return new lexer.Str(line, column, s);
//                case IDENTIFIER:
//                    Keyword k = keyword(s);
//                    if (k != null) return new Token(line, column, k);
//                    return new Identifier(line, column, s);
//            }
        }

        if (state.accept()) return new Token(line, column, (Symbol) state);
        throw new IllegalArgumentException("Invalid token: " + s);
    }

    // BINARY NODE CONVERSIONS
    public Node toNode(Node a, Node b) {
        if (!(this.kind instanceof Symbol)) throw new ExpectingException("binary operator", this);
        switch ((Symbol) this.kind) {
            case PLUS ->    { return new Arithmetic.Add(this, a, b); }
            case MINUS ->   { return new Arithmetic.Subtract(this, a, b); }
            case STAR ->    { return new Arithmetic.Multiply(this, a, b); }
            case SLASH ->   { return new Arithmetic.Divide(this, a, b); }
            case PERCENT -> { return new Arithmetic.Mod(this, a, b); }

            case AND_AND ->     { return new Logical.And(this, a, b); }
            case PIPE_PIPE ->   { return new Logical.Or(this, a, b); }

            case AND ->             { return new Bitwise.And(this, a, b); }
            case PIPE ->            { return new Bitwise.Or(this, a, b); }
            case CARET ->           { return new Bitwise.Xor(this, a, b); }
            case GREATER_GREATER -> { return new Bitwise.ShiftRight(this, a, b); }
            case LESS_LESS ->       { return new Bitwise.ShiftLeft(this, a, b); }

            case LESS -> { return new Compare.LessThan(this, a, b); }
            case GREATER -> { return new Compare.GreaterThan(this, a, b); }
            case LESS_EQUAL -> { return new Compare.LessEqual(this, a, b); }
            case GREATER_EQUAL -> { return new Compare.GreaterEqual(this, a, b); }
            case EQUAL_EQUAL -> { return new Compare.Equal(this, a, b); }
            case BANG_EQUAL -> { return new Compare.NotEqual(this, a, b); }

            case EQUAL ->           { return new Assignment.Assign(this, (Variable) a, b); }
            case PLUS_EQUAL ->      { return new Assignment.Add(this, (Variable) a, b); }
            case MINUS_EQUAL ->     { return new Assignment.Subtract(this, (Variable) a, b); }
            case STAR_EQUAL ->      { return new Assignment.Multiply(this, (Variable) a, b); }
            case SLASH_EQUAL ->     { return new Assignment.Divide(this, (Variable) a, b); }
            case PERCENT_EQUAL ->   { return new Assignment.Mod(this, (Variable) a, b); }
            case AND_EQUAL ->       { return new Assignment.And(this, (Variable) a, b); }
            case PIPE_EQUAL ->      { return new Assignment.Or(this, (Variable) a, b); }
            case CARET_EQUAL ->     { return new Assignment.Xor(this, (Variable) a, b); }

            default -> throw new ExpectingException("binary operator", this);
        }
    }

    // UNARY NODE CONVERSIONS
    public Node toNode(Node variable, boolean pre) {
        if (!(this.kind instanceof Symbol)) throw new ExpectingException("unary operator", this);
        switch ((Symbol) this.kind) {
            case PLUS_PLUS -> {
                return (pre) ? new Prefix.Increment(this, variable) : new Postfix.Increment(this, variable);
            }
            case MINUS_MINUS -> {
                return (pre) ? new Prefix.Decrement(this, variable) : new Postfix.Decrement(this, variable);
            }
            default -> throw new ExpectingException("unary operator", this);
        }
    }

    public Node toNode(Node variable) {
        if (!(this.kind instanceof Symbol)) throw new ExpectingException("unary operator", this);
        switch ((Symbol) this.kind) {
            case BANG -> { return new Prefix.Negation(this, variable); }
            case PLUS -> { return new Prefix.Identity(this, variable); }
            case MINUS -> { return new Prefix.Negative(this, variable); }
            default -> { throw new ExpectingException("unary operator", this); }
        }
    }


}
