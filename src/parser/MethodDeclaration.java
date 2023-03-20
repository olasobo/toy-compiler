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
}
