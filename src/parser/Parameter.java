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
}
