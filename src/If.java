public class If extends Statement {
    Node condition;
    Statement primary;
    Statement secondary;

    public If(Token token, Node condition, Statement primary, Statement secondary) {
        super(token);
        this.condition = condition;
        this.primary = primary;
        this.secondary = secondary;
    }
}
