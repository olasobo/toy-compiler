package semantic;

import parser.*;
import lexer.*;

import java.util.HashMap;

public class SymbolTable {
    String name;
    SymbolTable parent;
    HashMap<String, Node> symbols;

}
