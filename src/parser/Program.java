package parser;
import lexer.*;

public class Program extends Node {
    Identifier identifier;
    Block block;

    public Program(Token token, Identifier identifier, Block block) {
        super(token);
        this.identifier = identifier;
        this.block = block;
    }

    @Override
    public String treeString() {
        String result = "PROGRAM " + this.identifier.getName() + "\n";
        return result + this.block.indentedTree(1);
    }
}
