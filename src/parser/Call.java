package parser;

public class Call extends Statement {
    Variable method;
    Arguments arguments;

    public Call(Variable method, Arguments arguments) {
        super(method.token);
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public String treeString() {
        String result = "CALL " + method.getName() + "\n";
        result += (this.arguments == null) ? "" : this.arguments.indentedTree(1);

        return result;
    }
}
