package parser;
import lexer.*;

public abstract class Statement extends Node {
    public Statement(Token token) {
        super(token);
    }
}
