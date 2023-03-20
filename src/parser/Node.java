package parser;
import lexer.*;

public abstract class Node {
    Token token;

    public Node(Token token) {
        this.token = token;
    }
    public int line() {
        return token.line();
    }
    public int column() {
        return token.column();
    }
}
