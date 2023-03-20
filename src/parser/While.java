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
}
