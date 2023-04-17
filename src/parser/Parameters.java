package parser;

import java.util.ArrayList;
import java.util.List;

public class Parameters extends Node {
    private ArrayList<Parameter> parameters;

    public Parameters(Parameter parameter) {
        super(parameter.token);
        this.parameters = new ArrayList<>();
        this.parameters.add(parameter);
    }

    public Parameters(Parameter[] parameters) {
        super(parameters[0].token);
        this.parameters = new ArrayList<>(List.of(parameters));
    }

    public void add(Parameter parameter) {
        this.parameters.add(parameter);
    }

    @Override
    public String treeString() {
        String result = "PARAMETERS";
        for (Parameter parameter : this.parameters) {
            result += parameter.indentedTree(1);
        }

        return result;
    }
}
