package parser;
import lexer.*;

public class MethodDeclaration extends Statement {
    Type type;
    Identifier identifier;
    Parameters parameters;
    Block block;

    public MethodDeclaration(Type type, Identifier identifier, Parameters parameters, Block block) {
        super(identifier);
        this.type = type;
        this.identifier = identifier;
        this.parameters = parameters;
        this.block = block;
    }

    @Override
    public String treeString() {
        String result = "METHOD DECLARATION " + this.type.getType() + " " + identifier.getName() + "\n";
        result += this.parameters.indentedTree(1) + "\n";
        result += this.block.indentedTree(1);

        return result;
    }
}
