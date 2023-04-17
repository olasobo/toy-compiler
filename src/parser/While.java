package parser;
import lexer.*;

public class While extends Statement {
    Node condition;
    Statement statement;

    public While(Token token, Node condition, Statement statement) {
        super(token);
        this.condition = condition;
        this.statement = statement;
    }

    @Override
    public String treeString() {
        String result = "WHILE\n";
        result += this.condition.indentedTree(1);
        result += this.statement.indentedTree(2);

        return result;
    }
}
