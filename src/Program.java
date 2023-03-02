public class Program extends Node {
    Identifier identifier;
    Block block;

    public Program(Token token, Identifier identifier, Block block) {
        super(token);
        this.identifier = identifier;
        this.block = block;
    }
}
