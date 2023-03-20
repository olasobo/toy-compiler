package parser;
import lexer.*;

public class ExpectingException extends RuntimeException {
    public ExpectingException(Kind expecting, Token found) {
        this(expecting.toString(), found);
    }

    public ExpectingException(Parser.Constructs expecting, Token found) {
        this(expecting.name(), found);
    }

    public ExpectingException(String expecting, Token found) {
        super("Expecting " + expecting + ", found " + found.kind() + " at " + found.line() + ":" + found.column());
    }
}
