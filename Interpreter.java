import java.util.*;
import java.util.regex.*;

public class Interpreter {
    private static List<String> tokens;
    private static int currIndex;
    private static String currToken;
    private static Map<String, Integer> map;

    public static void main(String[] args) {
        String program = "a = 3";

        try {
            start(program);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void start(String program) {
        tokens = GetTokens(program);
        currIndex = 0;
        map = new HashMap<>();
        nextToken();
        program();
        System.out.println("Valid Program");
        for (Map.Entry<String, Integer> printMap : map.entrySet()) {
            System.out.println(printMap.getKey() + " = " + printMap.getValue());
        }
    }

    private static List<String> GetTokens(String input) {
        List<String> res = new ArrayList<>();
        String reg = "\\d+|[a-zA-Z_][a-zA-Z_0-9]*|[=+\\-*/();]|\\S";
        Pattern pattern = Pattern.compile(reg);
        Matcher match = pattern.matcher(input);
        while (match.find()) {
            res.add(match.group());
        }
        res.add("EndOfFile");
        return res;
    }

    private static void nextToken() {
        currToken = tokens.get(currIndex++);
    }

    private static void program() {
        while (!currToken.equals("EndOfFile")) {
            assignment();
        }
    }

    private static void assignment() {
        String currString = currToken;
        if (isIdentifier(currString)) {
            nextToken();
            if (currToken.equals("=")) {
                nextToken();
                int value = exp();
                if (currToken.equals(";")) {
                    map.put(currString, value);
                    nextToken();
                } else {
                    System.out.println("Invalid Program");
                    error("Expected ';'");
                }
            } else {
                System.out.println("Invalid Program");
                error("Expected '='");
            }
        } else {
            System.out.println("Invalid Program");
            error("Expected an Identifier");
        }
    }

    private static int exp() {
        int value = term();
        while (currToken.equals("+") || currToken.equals("-")) {
            if (currToken.equals("+")) {
                value += term();
            } else {
                value -= term();
            }
        }
        return value;
    }

    private static int term() {
        int value = fact();
        while (currToken.equals("*")) {
            value *= fact();
        }
        return value;
    }

    private static int fact() {
        int value;
        if (currToken.equals('(')) {
            nextToken();
            value = exp();
            if (currToken.equals(')')) {
                nextToken();
            } else {
                System.out.println("Invalid Program");
                error("Expected ')'");
                return 0;
            }
        } else if (isNumber(currToken)) {
            value = Integer.parseInt(currToken);
            nextToken();
        } else if (isIdentifier(currToken)) {
            String strValue = currToken;
            if (!map.containsKey(strValue)) {
                System.out.println("Invalid Program");
                error("Uninitialized variable '" + strValue + "'");
            }
            value = map.get(strValue);
            nextToken();
        } else if (currToken.equals("+")) {
            value = fact();
            nextToken();
        } else if (currToken.equals("-")) {
            value = -fact();
        } else {
            System.out.println("Invalid Program");
            error("Expected '('");
            return 0;
        }
        return value;
    }

    private static boolean isNumber(String token) {
        return token.matches("\\d+");
    }

    private static boolean isIdentifier(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z_0-9]*");
    }

    private static void error(String msg) {
        throw new RuntimeException(msg);
    }
}