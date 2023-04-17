package parser;
import lexer.*;

public class Type extends Node {
    Token type;
    Node length;
    boolean array;

    public Type(Token type) {
        this(type, false);
    }

    public Type(Token type, Node length) {
        this(type, true);
        this.length = length;
    }

    public Type(Token type, boolean array) {
        super(type);
        this.type = this.token;

        this.length = null;
        this.array = array;
    }

    @Override
    public String treeString() {
        return "TYPE " + this.type + "\n";
    }

    public Token getType() {
        return this.type;
    }
}
