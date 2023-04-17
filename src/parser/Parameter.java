package parser;
import lexer.*;

public class Parameter extends Node {
    Type type;
    Identifier identifier;

    public Parameter(Type type, Identifier identifier) {
        super(identifier);
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public String treeString() {
        String result = this.type + " " + identifier.getName() + "\n";

        return result;
    }
}
