package lexer;

import java.util.*;

import static errors.Sayop.*;
import static lexer.TokenState.*;
import static lexer.TokenType.*;

//lambda
@FunctionalInterface
interface TwoCharsOp{
    String createOp(char c1, char c2);
}

public class Lexer {

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static{
        KEYWORDS.put("SUGOD", START_PROG);
        KEYWORDS.put("KATAPUSAN", END_PROG);

        KEYWORDS.put("DAWAT", INPUT);
        KEYWORDS.put("IPAKITA", OUTPUT);

        KEYWORDS.put("MUGNA", VAR_DECLARATION);
        KEYWORDS.put("NUMERO", DATA_TYPE);
        KEYWORDS.put("LETRA", DATA_TYPE);
        KEYWORDS.put("TIPIK", DATA_TYPE);
        KEYWORDS.put("TINUOD", DATA_TYPE);
        KEYWORDS.put("PISI", DATA_TYPE);

        KEYWORDS.put("UG", LOG_AND);
        KEYWORDS.put("O", LOG_OR);
        KEYWORDS.put("DILI", LOG_NOT);

        KEYWORDS.put(null, NONE);     // wa man ni gamit oi, di man ni ma recognize
    }

    private static final List<LiteralChecker> literalCheckers = List.of(
            new LiteralChecker.BoolLC(),
            new LiteralChecker.CharLC(),
            new LiteralChecker.StringLC(),
            new LiteralChecker.IntLC(),
            new LiteralChecker.DoubleLC()
    );


    private final List<String> lines;
    private final List<List<Token>> tokenLines; //store tokens in each lines
    int currLine = 0;
    int position = -1;

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
    }

    public List<List<Token>> readlines(){

        for (int i = 0; i<lines.size(); i++){

//            System.out.println("Reading line: " + (i+1));
            currLine = i+1;
            String line = lines.get(i).trim();

            if(!line.isEmpty()){

                List<Token> lineToken = new ArrayList<>();

                tokenMKeywords(line, lineToken);
//                for (Token t : lineToken) t.setLine(i+1);
//                System.out.println(lineToken);
                if (!lineToken.isEmpty()) tokenLines.add(lineToken);
            }
        }
        // print all tokens
        // makita langa ng katag
        for (List<Token> tl : tokenLines) System.out.println(tl + "\n");
        return tokenLines;
    }

    //TRM (Terminal Symbols): Keywords and operators
    //this functions is for all main keywords
    private void tokenMKeywords(String line, List<Token> lineToken) {

        // skip comments, expected that a line starts with -- is a comment
        if (line.startsWith("--") || line.isBlank()) return;

        List<String> lexemes = new ArrayList<>(Arrays.asList(line.split("\\s+")));
        TokenType type;

        for (int i = 0; i < lexemes.size(); i++) {

            String lexeme = lexemes.get(i);

            if (KEYWORDS.containsKey(lexeme)) type = KEYWORDS.get(lexeme);
            else {
                if (lexeme.contains("IPAKITA") || lexeme.contains("DAWAT")) {
                    checkIO(lexeme, lineToken);
                    continue;
                }
                else if (similarKeyword(lexeme)) throw new LexicalError(currLine, "Dili maila na keyword '" + lexeme + "'. Imo gipasabot ba kay " + realKeyword(lexeme) + "?");
                else {
//                    System.out.println(String.join(" ", lexemes.subList(i, lexemes.size())));
                    tokenizeParts(String.join(" ", lexemes.subList(i, lexemes.size())), lineToken);
                    return;
                }
            }
            lineToken.add(new Token(type, lexeme));
            setLine(lineToken);
        }
    }

    // from here, i tokenize nato the smaller parts where need nato i read char by char
    // naa dire ang mga other types of tokens na dili keywords
    // start - starting index from like asa ta nag undang kay dire nata mag sugod nasad basa, but lage mga smaller parts na
    // end - pinaka last sa line
//    private void tokenizeParts(int start, int end, List<String> lexemes){
    private void tokenizeParts(String lexemes, List<Token> lineToken){

        StringBuilder lex = new StringBuilder();
        boolean isChar, isString;

        isChar = isString = false;

        TokenState ts = new TokenState();
        TwoCharsOp tco = (c1, c2) -> String.valueOf(c1) + c2;

        for (int i = 0; i<lexemes.length(); i++){

            STATE state = ts.tokenState(lexemes.charAt(i));

            switch(state){
                case STATE.WHITESPACE:
                    break;

                // literals, identifiers, and keywords
                case STATE.UPPER_CASE :
                    // assuming these are other keywords like UG, O, DILI
                    while (i < lexemes.length() && Character.isUpperCase(lexemes.charAt(i))){
                        if (i >= lexemes.length()) throw new LexicalError(currLine, "Unterminated character literal at position " + i);
                        lex.append(lexemes.charAt(i));
                        ++i;
                    }
                    --i;
                    addToken(lex, lineToken);
                    break;
                case STATE.LOWER_CASE:
                case STATE.UNDERSCORE:
                    //assume na variable/identifier ni siya
                    while (i < lexemes.length() && (Character.isLetterOrDigit(lexemes.charAt(i)) || lexemes.charAt(i) == '_')){
                        if (i >= lexemes.length()) throw new LexicalError(currLine,"Unterminated character literal at position " + i);
                        lex.append(lexemes.charAt(i));
                        ++i;
                    }
                    --i;
                    addToken(lex, lineToken);
                    break;
                case STATE.NUMBER:
                    // integer or decimal
                    while(i < lexemes.length() && (Character.isDigit(lexemes.charAt(i)) || lexemes.charAt(i) == '.')){
                        if (i >= lexemes.length()) throw new LexicalError(currLine,"Unterminated character literal at position " + i);

                        lex.append(lexemes.charAt(i));
                        ++i;
                    }
                    --i;
                    addToken(lex, lineToken);
                    break;

                // symbols
                case STATE.COLON:
                    lineToken.add(new Token(COLON, String.valueOf(lexemes.charAt(i))));
                    setLine(lineToken);
                    break;

                case STATE.BRACKET:
                    // DO NOT MODIFY! Unique ni sha
                    lineToken.add(new Token(BRACKET_OPEN, String.valueOf(lexemes.charAt(i))));
                    setLine(lineToken);
                    ++i;
                    if (i >= lexemes.length() ) throw new LexicalError(currLine, "Wala nahuman ang bracket nga escape code");
                    if (lexemes.charAt(i+1) == ']') lineToken.add(new Token(ESCAPE_CODE, String.valueOf(lexemes.charAt(i))));
                    ++i;
                    setLine(lineToken);
                    lineToken.add(new Token(BRACKET_CLOSE, String.valueOf(lexemes.charAt(i))));
                    setLine(lineToken);
                    break;
                case STATE.COMMA:
                    lineToken.add(new Token(COMMA, String.valueOf(lexemes.charAt(i))));
                    setLine(lineToken);
                    break;
                case STATE.CONCAT:
                    lineToken.add(new Token(CONCAT, String.valueOf(lexemes.charAt(i))));
                    setLine(lineToken);
                    break;
                case STATE.NEW_LINE:
                    lineToken.add(new Token(NEW_LINE, String.valueOf(lexemes.charAt(i))));
                    setLine(lineToken);
                    break;
                case STATE.SINGLE_Q:
                    lex.append(lexemes.charAt(i));
                    isChar = true;
                    ++i;
                    while (isChar){
                        if (i >= lexemes.length()) throw new LexicalError(currLine,"Unterminated character literal at position " + i);
                        if (lexemes.charAt(i) == '\'') isChar = false;
                        lex.append(lexemes.charAt(i));
                        ++i;
                    }
                    addToken(lex, lineToken);
                    break;
                case STATE.DOUBLE_Q:
                    lex.append(lexemes.charAt(i));
                    isString = true;
                    ++i;
                    while (isString){
                        if (i >= lexemes.length()) throw new LexicalError(currLine,"Unterminated character literal at position " + i);
                        if (lexemes.charAt(i) == '"') isString = false;
                        lex.append(lexemes.charAt(i));
                        ++i;
                    }
                    addToken(lex, lineToken);
                    break;

                // operators
                case STATE.ARITH_OP:
                    addOperations(String.valueOf(lexemes.charAt(i)), lineToken); break;
                case STATE.MINUS:
                    if (i+1 >= lexemes.length()) throw new RuntimeError(currLine, "Unterminated character literal");
                    if (lexemes.charAt(i+1) == '-'){
                        lineToken.add(new Token(COMMENT, lexemes.substring(i)));
                        setLine(lineToken);
                        return;
                    }
                    addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                case STATE.GT:
                    if (lexemes.length() > i+1 && lexemes.charAt(i+1) == '>' ||lexemes.charAt(i+1) == '=') addOperations(tco.createOp(lexemes.charAt(i),lexemes.charAt(++i)), lineToken);
                    else addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                case STATE.LT:
                    if (lexemes.length() > i+1 && lexemes.charAt(i+1) == '=') addOperations(tco.createOp(lexemes.charAt(i),lexemes.charAt(++i)), lineToken);
                    else addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                case STATE.EQUALS:
                    if (lexemes.length() > i+1 && lexemes.charAt(i+1) == '=') addOperations(tco.createOp(lexemes.charAt(i),lexemes.charAt(++i)), lineToken);
                    else addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                default:
                    // error
                    throw new LexicalError(currLine, "Wala mailhi na character: " + lexemes.charAt(i));
            }
        }
    }

    //for identifiers and literals lang ni sha oi
    private void addToken(StringBuilder str, List<Token> lineToken){
//        System.out.println(str);
        if (str.isEmpty()) return;

        String lexeme = str.toString();
        boolean foundType = false;

        switch (lexeme){
            case "UG":
                lineToken.add(new Token(LOG_AND, lexeme));
                foundType = true;
                break;
            case "O":
                lineToken.add(new Token(LOG_OR, lexeme));
                foundType = true;
                break;
            case "DILI":
                lineToken.add(new Token(LOG_NOT, lexeme));
                foundType = true;
                break;
        }

        if (!foundType) {
            for (LiteralChecker lc : literalCheckers) {
//            System.out.println(lexeme + " -> " + lc.getClass().getSimpleName());
                if (lc.isLiteral(lexeme, currLine)) {
                    lineToken.add(lc.addToken(lexeme));
                    foundType = true;
                    break;
                }
            }
        }

        if (!foundType) {
            lineToken.add(new Token(IDENTIFIER, lexeme)); //if variable
            setLine(lineToken);
            str.setLength(0);
            return;
        }
        setLine(lineToken);
        str.setLength(0);
    }

    private void addOperations(String token, List<Token> lineToken){
        TokenType type = NONE;
        switch (token){
            //arithmetic
            case "+":
                type = ARITH_ADD; break;
            case "-":
                type = ARITH_MINUS; break;
            case "*":
                type = ARITH_MULT; break;
            case "/":
                type = ARITH_DIV; break;
            case "%":
                type = ARITH_MOD; break;
            case "(":
                type = ARITH_OPEN_P; break;
            case ")":
                type = ARITH_CLOSE_P; break;

            // relational
            case ">":
                type = ARITH_GT; break;
            case ">=":
                type = ARITH_GOE; break;
            case "<":
                type = ARITH_LT; break;
            case "<=":
                type = ARITH_LOE; break;
            case "<>":
                type = ARITH_NOT_EQUAL; break;
            case "==":
                type = ARITH_EQUAL; break;

            // assignment operation
            case "=":
                type = ASS_OP; break;
        }
        if (type == NONE) throw new LexicalError(currLine,"Dili maila na operator '" + token + "'");
        lineToken.add(new Token(type, token));
        setLine(lineToken);
    }

    /*
        para sa mga i/o tungod naay cases na instead of butang space between colon and keyword, magka duol sila
            Example:
                IPAKITA:        -- instead of IPAKITA :
                DAWAT:

        the rest need gyud silag space between
    */
    private void checkIO(String lexeme, List<Token> lineToken){
        if (lexeme.endsWith(":")) {
            lineToken.add(new Token(KEYWORDS.get(lexeme.contains("IPAKITA") ? "IPAKITA" : "DAWAT"), lexeme.substring(0, lexeme.length() - 1)));
            setLine(lineToken);
            lineToken.add(new Token(COLON, lexeme.substring(lexeme.length()-1)));
            setLine(lineToken);
        } else throw new LexicalError(currLine,"Dili maila na keyword '" + lexeme + "'. " +
                (lexeme.contains("IPAKITA") ? "Imo gipasabot ba kay 'IPAKITA'?" :
                        (lexeme.contains("DAWAT") ? "Imo gipasabot ba kay 'DAWAT'?" : "")
                )
        );

    }

    private boolean similarKeyword(String lexeme){
//        System.out.println("na print ko -- similar keyword");
        return realKeyword(lexeme) != "wala";
    }

    //aaa rar wait kalang, need to enhance this
    private String realKeyword(String lexeme){
//        System.out.println("na print ko -- real keyword");
        String[] keywords = {
                "SUGOD", "KATAPUSAN", "MUGNA", "NUMERO", "LETRA",
                "TIPIK", "TINUOD", "PISI", "DAWAT", "IPAKITA"
        };

        String prefix = lexeme.substring(0, Math.min(lexeme.length(), 3));

        for (String keyword : keywords) {
            if (keyword.startsWith(prefix)) {
                return keyword;
            }
        }

        return "wala";
    }

    private void setLine(List<Token> lineToken){
        lineToken.getLast().setLine(currLine);
    }

}
