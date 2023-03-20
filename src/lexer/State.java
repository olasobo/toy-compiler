package lexer;

import parser.*;

public interface State {
    State next(char c);
    boolean accept();
}
