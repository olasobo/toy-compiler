package lexer;

import parser.*;

public abstract class Value extends Token {

    public Value(int line, int column, Kind kind) {
        super(line, column, kind);
    }

    public enum Type implements Kind {
        IDENTIFIER,
        INTEGER,
        CHARACTER,
        STRING
    }
}
