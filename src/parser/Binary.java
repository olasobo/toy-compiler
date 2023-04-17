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
    public String treeString() {
        String result = this.token.kind() + " BINARY\n";
        result += left.indentedTree(1) + "\n";
        result += right.indentedTree(1);

        return result;
    }

    @Override
    public String toString() {
        return left.toString() + " " + token.kind() + " " + right.toString();
    }
}
