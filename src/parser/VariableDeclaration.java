package parser;
import lexer.*;

public class VariableDeclaration extends Statement {
    Type type;
    Identifier identifier;

    public VariableDeclaration(Type type, Identifier identifier) {
        super(identifier);
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public String treeString() {
        return "VARIABLE DECLARATION " + this.type.getType() + " " + identifier.getName() + "\n";
    }
}
