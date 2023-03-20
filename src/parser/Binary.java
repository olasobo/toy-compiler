package parser;
import lexer.*;

public abstract class Binary extends Node {
    Node left;
    Node right;

    public Binary(Kind kind, Token token, Node left, Node right) {
        super(token);

        if (token.kind() != kind) throw new ExpectingException(kind, token);

        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + " " + token.kind() + " " + right.toString();
    }
}
