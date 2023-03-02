public abstract class Unary extends Node {
    Node child;

    public Unary(Kind kind, Token token, Node child) {
        super(token);

        if (token.kind != kind) throw new ExpectingException(kind, token);

        this.child = child;
    }
}
