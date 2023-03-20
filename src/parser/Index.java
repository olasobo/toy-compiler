package parser;
import lexer.*;

public class Index extends Node {
    Variable variable;
    Node index;

    public Index(Variable variable, Node index) {
        super(variable.token);
        this.variable = variable;
        this.index = index;
    }
}
