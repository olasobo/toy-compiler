package parser;
import lexer.*;

public abstract class Leaf extends Node {
    public Leaf(Token token) {
        super(token);
    }

}
