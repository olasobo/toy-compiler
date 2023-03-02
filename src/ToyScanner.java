import java.util.Scanner;

public class ToyScanner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int lineNumber = 1;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String tokenString = line.strip();
            try {
                Token token = Token.parse(lineNumber, 0, tokenString);
                System.out.println(token);
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + tokenString);
            }
            lineNumber++;

        }
    }
}
