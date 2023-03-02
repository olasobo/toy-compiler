public class VariableDeclaration extends Statement {
    Type type;
    Identifier identifier;

    public VariableDeclaration(Type type, Identifier identifier) {
        super(identifier);
        this.type = type;
        this.identifier = identifier;
    }
}
