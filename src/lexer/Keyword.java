package lexer;

public enum Keyword implements Kind {
    PROGRAM,
    IF,
    ELSE,
    WHILE,
    RETURN,
    INT,
    CHAR,
    BOOLEAN,
    VOID;

    public static Keyword find(String s) {
        for (Keyword k : Keyword.values()) {
            if (s.equals(k.name().toLowerCase())) return k;
        }

        return null;
    }
}
