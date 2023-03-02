public class Return extends Statement {
    Node expression;
    public Return(Token token, Node expression) {
        super(token);
        this.expression = expression;
    }
}
