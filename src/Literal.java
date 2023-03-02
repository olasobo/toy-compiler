public class Literal extends Leaf {

    public Literal(Value token) {
        super(token);
    }

    @Override
    public String toString() {
        return token.toString();
    }

    public int line() {
        return token.line;
    }
    public int column() {
        return token.column;
    }
}
