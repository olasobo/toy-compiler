package lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Lexer {
    private Scanner scanner;
    private String filename;
    //private ArrayList<String> previous = new ArrayList<>();
    private String current;

    private boolean end = false;
    private boolean newLine;

    private static final int START_LINE = 1, START_COLUMN = 1;
    private int line = START_LINE-1;
    private int column = START_COLUMN;
    private int[] start = new int[]{-1, -1};

    private State state;
    private StringBuilder value;

    private State lastAccepted; // state of last valid token
    private String lastString;  // string representation of last valid token
    private int lastLine;
    private int lastColumn;

    private String lineProgression; // DEBUGGING

    // PUBLIC METHODS

    public static void main(String[] args) {
        System.out.println(args[0]);
        Lexer lexer = new Lexer(args[0]);
        while (lexer.hasNextToken()) {
            System.out.println(lexer.nextToken());

            System.out.println(lexer.lineProgression);
            System.out.println();
        }
    }

    public Lexer() {
        this.scanner = new Scanner(System.in);
        //this.previous = new ArrayList<>();
        this.nextLine();
        this.resetValues();
    }

    public Lexer(String filename) {
        try {
            File file = new File(filename);
//            System.out.println(file.exists());
            this.scanner = new Scanner(file);
            this.filename = filename;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Invalid filename");
        }
        //this.previous = new ArrayList<>();
        //System.out.println("size: " + this.previous.size());
        this.nextLine();
        this.resetValues();
    }

//    public void setPosition(int line, int column) {
//        if (line > this.line || (line == this.column && column > this.column)) throw new IllegalArgumentException("line and column must have been previously visited");
//
//        if (line < this.line) this.current = this.previous.get(line - START_LINE + 1);
//        this.line = line;
//        this.column = column;
//    }

    public String getFilename() {
        return (this.filename == null) ? "no filename" : this.filename;
    }

    public Token nextToken() {
        this.resetValues();
        //this.addLine("before");

        do {
            this.setStart();
            this.advanceState();
            if (this.state.accept()) this.saveValues();
        } while (this.continueToken());

        this.setPosition();
        //this.addLine("after");
        return this.tokenValue();
    }

    public boolean hasNextToken() {
        return !this.end;
    }


    // HELPER METHODS

    private void resetValues() {
        this.state = Symbol.START;
        this.value = new StringBuilder();

        this.newLine = false;

        this.lastAccepted = null;
        this.lastString = "";
        this.lastLine = -1;
        this.lastColumn = -1;

        this.lineProgression = "";
    }

    private void setStart() {
        if (this.state == Symbol.START) {
            this.start[0] = this.line;
            this.start[1] = this.column;
        }
    }

    private void advanceState() {
        char c = this.current.charAt(this.column - START_COLUMN);
        this.state = this.state.next(c);
        if (this.state != Symbol.START && this.state != Symbol.ERROR) this.value.append(c);
    }

    private void saveValues() {
        this.lastAccepted = this.state;
        this.lastString = this.value.toString();
        this.lastLine = this.line;
        this.lastColumn = this.column;
    }

    private boolean continueToken() {
        boolean reachedComment = this.commentManaged();
        if (this.end)       return false;
        if (reachedComment) return true;

        boolean noTerminalSymbol = this.state != Symbol.ERROR && !this.nextCharacter();
        return noTerminalSymbol;
    }

    private boolean commentManaged() {
        if (this.state != Symbol.SLASH_SLASH) return false;
        this.nextLine();
        this.resetValues();

        this.addLine("new line");
//        System.out.println("comment handled");

        return true;
    }

    private void nextLine() {
        this.newLine = true;
        this.column = START_COLUMN;

        try {
            do {
                this.current = scanner.nextLine();
                this.line++;
            } while (this.current.equals("") || this.current.split(" ").length == 0);
        } catch (NoSuchElementException e) { // end of file
            this.end = true;
        }

//        try {
//            do {
//                //this.previous.add(this.current);
//                this.current = scanner.nextLine();
//                this.line++;
//            } while (this.current.equals(""));
//        } catch (NoSuchElementException e) { // end of file
//            this.end = true;
//        }
    }

    // returns whether the end of a line has been reached
    private boolean nextCharacter() {
        this.column++;

        // reached end of the line
        if (this.column > current.length()) {
//            System.out.println("new line");
            this.nextLine();
        }

        return this.newLine;
    }

    private void setPosition() {
        if (this.newLine) return;
        if (this.lastAccepted != null) {
            this.line = this.lastLine;
            this.column = this.lastColumn;
        }
        this.nextCharacter();
    }

    private Token tokenValue() {
        if (this.lastAccepted == null)                      return new Token(this.start[0], this.start[1], (this.end) ? Symbol.END : Symbol.ERROR);
        if (this.lastAccepted instanceof Int.IntState)      return new Int(this.start[0], this.start[1]);
        if (this.lastAccepted instanceof Char.CharState)    return new Char(this.start[0], this.start[1]);
        if (this.lastAccepted instanceof Str.StrState)      return new Str(this.start[0], this.start[1], this.lastString);
        if (this.lastAccepted instanceof Identifier.IdState) {
            Keyword k = Keyword.find(this.lastString);
            return (k == null) ? new Identifier(this.start[0], this.start[1], this.lastString.length()) : new Token(this.start[0], this.start[1], k);
        }

        return new Token(this.start[0], this.start[1], (Symbol) this.lastAccepted);
    }


    // DEBUGGING METHODS

    private static boolean whitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

    private boolean atPosition(int l, int c) {
        return this.line == l && this.column == c;
    }

    private String positionString() {
        return "[" + this.line + ", " + this.column + "]";
    }

    private String lineInformation() {
        return this.positionString() + "\t| " + this.current.substring(this.column-1).stripLeading();
    }

    private void addLine(String description) {
        this.lineProgression += String.format("%10s", description + ": ") + this.lineInformation() + "\n";
    }
}
