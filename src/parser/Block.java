package parser;

import java.util.ArrayList;
import lexer.*;

public class Block extends Statement {
    ArrayList<Statement> statements;
    public Block(Token token, Statement statement) {
        super(token);
        statements = new ArrayList<>();
        statements.add(statement);
    }

    public Block(Token token) {
        super(token);
        statements = new ArrayList<>();
    }

    public void add(Statement statement) {
        if (this.token == null) this.token = statement.token;
        statements.add(statement);
    }

    @Override
    public String treeString() {
        String result = "BLOCK\n";
        for (Statement statement : this.statements) {
            result += statement.indentedTree(1) + "\n";
        }

        return result;
    }
}
