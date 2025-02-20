package lexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Lexer {

    private static final List<String> KEYWORDS = List.of(
            // common
            "SUGOD",
            "KATAPUSAN",

            "MUGNA",        // : variable declaration
            "IPAKITA",      // : print
            "DAWAT",        // : get input
            "PUNDOK",       // basta tapok rani sila, ma gamit sa conditional ug sa loops

            // conditional
            "KUNG",         // : if
            "WALA",         // : else (KUNG WALA), or default (WALA)
            "DILI",         // : else if (KUNG DILI), not - logical operator (DILI)
            "ILISAN",       // : switch
            "KASO",         // : case

            // loops
            "ALANG",         // : for - supposedly "ALANG SA" ni noh, pero kay token man, word for word, i add lang nako si "SA" unya
            "SAMTANG",       // : while
            "BUHAT",         // : do

            // logical operators
            "UG",            // : and
            "O"              // : or
    );

    private static final List<String> DATA_TYPES = List.of(
            "NUMERO",     // : int
            "LETRA",      // : char
            "TINUOD",     // : boolean
            "TIPIK",      // : double or with decimals, double nalang ni oi
            "PISI"        // : string
    );

    private final List<String> lines;
    private final List<Token> tokenLines;

//    in every list, if mag start kag indent, wa rashay pake
//    when using .contains(), pwede kaau ang nay space

    /*
        * the lines (or position/index per line) starts at 0
        * each index serve as the position (or line)
        * so, if mag plan na mu throw an error
        *       let's say:
        *           error at line 5
        * mag plus 1 -- a must if mag plano man ka ani
    */

    public Lexer(List<String> lines) {
        this.lines = lines;
        this.tokenLines = new ArrayList<>();
        System.out.println(this.lines);
    }

    public List<Token> tokenize(){

        for (String line : lines){
            processLine(line.trim());   //di ko mu basa sa way laman na line, that's not my priority
            System.out.println("reading line: " + (lines.indexOf(line) + 1));
            System.out.println(tokenLines);
        }

        return tokenLines;
    }

    public void processLine(String line){
//        if (line.startsWith("--")){
//            tokenLines.add(new Token(TokenType.COMMENT, line));
//            return;
//        }

        // ambot pero di lang cguro nako basahon kng mag sugod mag comment
        // skip lang bha
        if (line.startsWith("--")) return;

        List<String> tokens = new ArrayList<>(Arrays.asList(line.split("\\s+")));      // split by whitespaces, split into tokens
        for (String token : tokens){
//            System.out.println("token: " + token);
            if (DATA_TYPES.contains(token)) continue;   //  skip if data type, the keyword 'MUGNA' will handle that

            // if token is a keyword
            // ma'am, nganu na man gyud space imo keyword TTOTT
            if (KEYWORDS.contains(token)){

                switch (token){
                    case "MUGNA":   // i sagol, bale i store siya as 'MUGNA NUMERO' etc. continue bow
                        addDataType(tokens); break;
                    // might add a condition for 'KUNG' kay naa man 'KUNG DILI' etc., also for 'ALANG SA'
                    case "KUNG":
                        completeCondition(tokens); break;
                    case "ALANG":
                        completeFor(tokens); break;
                    default:
                        tokenLines.add(new Token(TokenType.KEYWORD, token)); // kani kay para for 1 word lamang, like 'SUGOD'
                }

            }

            // if token a variable
            else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) tokenLines.add(new Token(TokenType.IDENTIFIER, token));

            // if token is a value
            else if (token.matches("[0-9]+")) tokenLines.add(new Token(TokenType.NUMBER, token));
            else if (token.matches("\"[^\"]*\"")) tokenLines.add(new Token(TokenType.STRING, token));
            else if (token.matches("'[^']'")) tokenLines.add(new Token(TokenType.CHARACTERS, token));

            // if special symbols
            else if (token.equals("&") || token.equals("$") || token.equals("[")) tokenLines.add(new Token(TokenType.SYMBOL, token));

            // if mid-line comments
//            else if (token.equals("--")) {
//                tokenLines.add(new Token(TokenType.COMMENT, token));
//                return;
//            }

            else    //dire na part kay kanang wa sila gi separate by spaces, so dire mag handle sa no space TTOTT
                System.out.println("token: " + token);
        }

    }

    private void addDataType(List<String> token) throws IllegalArgumentException{
        System.out.println("Check Data Type: " + token.get(1));

        if (token.size() < 2) throw new IllegalArgumentException("Walay data type! Palihug kog butang ( NUMERO, LETRA, TIPIK, TINUOD, PISI )");
        if (DATA_TYPES.contains(token.get(1))) tokenLines.add(new Token(TokenType.KEYWORD, token.get(0) + " " +token.get(1)) );      // keyword si 'MUGNA NUMERO' etc.
        // else throw an exception na mu attempt shag create og variable pero walay data type
        else throw new IllegalArgumentException("Sayop: " + token.get(1) + " kay dili data type");
    }

    private void completeCondition(List<String> token) throws IllegalArgumentException {
        if (KEYWORDS.contains(token.get(1))) {
            switch (token.get(1)){
                case "WALA":
                case "DILI":
                    tokenLines.add(new Token(TokenType.KEYWORD, token.get(0) + " " + token.get(1)) );
                default:
                    if (token.get(1).charAt(0) == '(') tokenLines.add(new Token(TokenType.KEYWORD, token.get(0)));
                    else throw new IllegalArgumentException("Sayop: wa ko kaila ni " + token.get(0) );
            }
        }
    }

    private void completeFor(List<String> token) throws IllegalArgumentException {
        if (token.get(1).equals("SA")) tokenLines.add(new Token(TokenType.KEYWORD, token.get(0) + " " + token.get(1)));
        else throw new IllegalArgumentException("Sayop: Kulangan ka og 'SA'");
    }
}
