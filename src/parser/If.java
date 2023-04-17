package parser;
import lexer.*;

public class If extends Statement {
    Node condition;
    Statement primary;
    Statement secondary;

    public If(Token token, Node condition, Statement primary, Statement secondary) {
        super(token);
        this.condition = condition;
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public String treeString() {
        String result = "IF\n";
        result += this.condition.indentedTree(1);
        result += this.primary.indentedTree(2) + "\n";
        result += (this.secondary == null) ? "" : this.secondary.indentedTree(2);

        return result;
    }
}
