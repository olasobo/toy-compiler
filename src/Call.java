public class Call extends Node {
    Variable method;
    Arguments arguments;

    public Call(Variable method, Arguments arguments) {
        super(method.token);
        this.method = method;
        this.arguments = arguments;
    }
}
