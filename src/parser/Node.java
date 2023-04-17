package parser;
import lexer.*;

public class Node {
    final String indentString = " ";
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

    protected String treeString() {
        return this.token.toString() + "\n";
    }

    public String indentedTree() {
        return this.indentedTree(0);
    }
    public String indentedTree(int n) {
        String indent = this.indentString.repeat(n);
        String[] lines = this.treeString().split("\n");

        String result = indent;
        for (int i = 0; i < lines.length; i++) result += lines[i] + "\n" + (i == lines.length-1 ? "" : indent);

        return result;
    }

//    protected String newLine(int indent) {
//        return "\n" + this.indentString.repeat(indent);
//    }
}
