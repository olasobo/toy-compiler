package parser;
import lexer.*;

import java.util.ArrayList;
import java.util.List;

public class Arguments extends Node {
    private ArrayList<Node> arguments;

    public Arguments(Node node) {
        super(node.token);
        this.arguments = new ArrayList<>();
        this.arguments.add(node);
    }

    public Arguments(Node[] arguments) {
        super(arguments[0].token);
        this.arguments = new ArrayList<>(List.of(arguments));
    }

    public void add(Node node) {
        this.arguments.add(node);
    }
}
