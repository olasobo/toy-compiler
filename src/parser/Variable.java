package parser;
import lexer.*;

import java.util.HashMap;

public class Variable extends Leaf {
    HashMap<Identifier, Value> scope;
    Identifier[] path;

    public Variable(Identifier name, HashMap<Identifier, Value> scope) {
        super(name);
        this.scope = scope;
    }

    @Override
    public String toString() {
        return this.token.toString();
    }

    public Variable(Identifier name, Identifier[] path) {
        super(name);
        this.path = path;
    }

    public Variable(Identifier name) {
        super(name);
        this.path = null;
    }
}
