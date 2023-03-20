package parser;
import lexer.*;

import java.util.ArrayList;

public class Block extends Statement {
    ArrayList<Statement> statements;
    public Block(Statement statement) {
        super(statement.token);
        statements = new ArrayList<>();
        statements.add(statement);
    }

    public Block() {
        super(null);
        statements = new ArrayList<>();
    }

    public void add(Statement statement) {
        statements.add(statement);
    }
}
