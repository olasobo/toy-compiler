package parser;
import lexer.*;

public class Return extends Statement {
    Node expression;
    public Return(Token token, Node expression) {
        super(token);
        this.expression = expression;
    }

    @Override
    public String treeString() {
        return "RETURN\n" + this.expression.indentedTree(1);
    }
}
