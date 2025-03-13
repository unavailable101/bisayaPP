package lexer;

import lexer.literals.*;

import java.util.*;

//basic features sa ta hehe, later na ang conditional and loops
//mas dako trabahoon ang basics


//lambda
@FunctionalInterface
interface TwoCharsOp{
    String createOp(char c1, char c2);
}

public class Lexer {

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static{
        KEYWORDS.put("SUGOD", TokenType.START_PROG);
        KEYWORDS.put("KATAPUSAN", TokenType.END_PROG);

        KEYWORDS.put("DAWAT", TokenType.INPUT);
        KEYWORDS.put("IPAKITA", TokenType.OUTPUT);

        KEYWORDS.put("MUGNA", TokenType.VAR_DECLARATION);
        KEYWORDS.put("NUMERO", TokenType.DATA_TYPE);
        KEYWORDS.put("LETRA", TokenType.DATA_TYPE);
        KEYWORDS.put("TIPIK", TokenType.DATA_TYPE);
        KEYWORDS.put("TINUOD", TokenType.DATA_TYPE);
        KEYWORDS.put("PISI", TokenType.DATA_TYPE);

        KEYWORDS.put("UG", TokenType.LOG_AND);
        KEYWORDS.put("O", TokenType.LOG_OR);
        KEYWORDS.put("DILI", TokenType.LOG_NOT);

        KEYWORDS.put(null, TokenType.NONE);     // wa man ni gamit oi, di man ni ma recognize
    }

    private static final List<LiteralChecker> literalCheckers = List.of(
            new BoolLC(),
            new CharLC(),
            new StringLC(),
            new IntLC(),
            new DoubleLC()
    );


    private final List<String> lines;
    private final List<List<Token>> tokenLines; //store tokens in each lines

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
//        System.out.println(this.lines);
    }

    public List<List<Token>> readlines(){

        for (int i = 0; i<lines.size(); i++){

//            System.out.println("Reading line: " + (i+1));

            String line = lines.get(i).trim();

            if(!line.isEmpty()){

                List<Token> lineToken = new ArrayList<>();

                tokenMKeywords(line, lineToken);
                for (Token t : lineToken) t.setLine(i+1);
//                System.out.println(lineToken);
                if (!lineToken.isEmpty()) tokenLines.add(lineToken);
            }
        }
        for (List<Token> tl : tokenLines) System.out.println(tl);
        return tokenLines;
    }

    //TRM (Terminal Symbols): Keywords and operators
    //this functions is for all main keywords
    private void tokenMKeywords(String line, List<Token> lineToken) {

        // skip comments, expected that a line starts with -- is a comment
        if (line.startsWith("--") || line.isBlank()) return;

        List<String> lexemes = new ArrayList<>(Arrays.asList(line.split("\\s+")));

        for (int i = 0; i < lexemes.size(); i++) {
            String lexeme = lexemes.get(i);

            switch (lexeme) {
                case "SUGOD":
                    lineToken.add(new Token(TokenType.START_PROG, lexeme));
                    break;
                case "MUGNA":
                    lineToken.add(new Token(TokenType.VAR_DECLARATION, lexeme));
                    break;
                case "NUMERO":
                case "LETRA":
                case "TINUOD":
                case "TIPIK":
                case "PISI":
                    lineToken.add(new Token(TokenType.DATA_TYPE, lexeme));
                    break;
                case "DAWAT":
                    lineToken.add(new Token(TokenType.INPUT, lexeme));
                    break;
                case "IPAKITA":
                    lineToken.add(new Token(TokenType.OUTPUT, lexeme));
                    break;
                default:
                    if (lexeme.contains("IPAKITA") || lexeme.contains("DAWAT")) checkIO(lexeme, lineToken);
                    else {
                        tokenizeParts(String.join(" ", lexemes.subList(lexemes.indexOf(lexeme), lexemes.size())), lineToken);
                        return;
                    }
            }
        }
    }

    // from here, i tokenize nato the smaller parts where need nato i read char by char
    // naa dire ang mga other types of tokens na dili keywords
    // start - starting index from like asa ta nag undang kay dire nata mag sugod nasad basa, but lage mga smaller parts na
    // end - pinaka last sa line
//    private void tokenizeParts(int start, int end, List<String> lexemes){
    private void tokenizeParts(String lexemes, List<Token> lineToken){

        StringBuilder lexeme = new StringBuilder();
        boolean isChar, isString, isEscape;

        isEscape = isChar = isString = false;

        TokenState ts = new TokenState();
        TwoCharsOp tco = (c1, c2) -> String.valueOf(c1) + c2;

        char whitespace = ' ';

        for (int i = 0; i<lexemes.length(); i++){

            TokenState.STATE state = ts.tokenState(lexemes.charAt(i));


            switch(state){

                case TokenState.STATE.WHITESPACE:
                    break;

                // literals, identifiers, and keywords
                case TokenState.STATE.UPPER_CASE :
                    // assuming these are other keywords like UG, O, DILI
                    while (i < lexemes.length() && Character.isUpperCase(lexemes.charAt(i))){
                        // LexicalException gyud ni siya
                        if (i >= lexemes.length()) throw new IllegalArgumentException("Unterminated character literal at position " + i);
                        lexeme.append(lexemes.charAt(i));
                        ++i;
                    }
                    --i;
                    addToken(lexeme, lineToken, isEscape);
                    break;
                case TokenState.STATE.LOWER_CASE:
                case TokenState.STATE.UNDERSCORE:
                    //assume na variable/identifier ni siya
                    while (i < lexemes.length() && (Character.isLetterOrDigit(lexemes.charAt(i)) || lexemes.charAt(i) == '_')){
                        // LexicalException gyud ni siya
                        if (i >= lexemes.length()) throw new IllegalArgumentException("Unterminated character literal at position " + i);
                        lexeme.append(lexemes.charAt(i));
                        ++i;
                    }
                    --i;
                    addToken(lexeme, lineToken, isEscape);
                    break;
                case TokenState.STATE.NUMBER:
                    // integer or decimal
                    while(i < lexemes.length() && (Character.isDigit(lexemes.charAt(i)) || lexemes.charAt(i) == '.')){
                        // LexicalException gyud ni siya
                        if (i >= lexemes.length()) throw new IllegalArgumentException("Unterminated character literal at position " + i);

                        lexeme.append(lexemes.charAt(i));
                        ++i;
                    }
                    --i;
                    addToken(lexeme, lineToken, isEscape);
                    break;

                // symbols
                case TokenState.STATE.BRACKET:
                    // for escape
                    // unya nalang bitaw ni si brackets kay i can't think straight, char
                    isEscape = true;
                    lineToken.add(new Token(TokenType.BRACKET_OPEN, String.valueOf(lexemes.charAt(i))));
                    break;
                case TokenState.STATE.COMMA:
                    lineToken.add(new Token(TokenType.COMMA, String.valueOf(lexemes.charAt(i)))); break;
                case TokenState.STATE.CONCAT:
                    lineToken.add(new Token(TokenType.CONCAT, String.valueOf(lexemes.charAt(i)))); break;
                case TokenState.STATE.NEW_LINE:
                    lineToken.add(new Token(TokenType.NEW_LINE, String.valueOf(lexemes.charAt(i)))); break;
                case TokenState.STATE.SINGLE_Q:
                    lexeme.append(lexemes.charAt(i));
                    isChar = true;
                    ++i;
                    while (isChar){
                        // LexicalException gyud ni siya
                        if (i >= lexemes.length()) throw new IllegalArgumentException("Unterminated character literal at position " + i);
                        if (lexemes.charAt(i) == '\'') isChar = false;
                        lexeme.append(lexemes.charAt(i));
                        ++i;
                    }
                    addToken(lexeme, lineToken, isEscape);
                    break;
                case TokenState.STATE.DOUBLE_Q:
                    lexeme.append(lexemes.charAt(i));
                    isString = true;
                    ++i;
                    while (isString){
                        // LexicalException gyud ni siya
                        if (i >= lexemes.length()) throw new IllegalArgumentException("Unterminated character literal at position " + i);
                        if (lexemes.charAt(i) == '"') isString = false;
                        lexeme.append(lexemes.charAt(i));
                        ++i;
                    }
                    addToken(lexeme, lineToken, isEscape);
                    break;

                // operators
                case TokenState.STATE.ARITH_OP:
                    addOperations(String.valueOf(lexemes.charAt(i)), lineToken); break;
                case TokenState.STATE.MINUS:
                    if (lexemes.charAt(i+1) == '-'){
                        lineToken.add(new Token(TokenType.COMMENT, lexemes.substring(i)));
                        return;
                    }
                    addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                case TokenState.STATE.GT:
                    if (lexemes.charAt(i+1) == '>' ||lexemes.charAt(i+1) == '=') addOperations(tco.createOp(lexemes.charAt(i),lexemes.charAt(++i)), lineToken);

                    else addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                case TokenState.STATE.LT:
                    if (lexemes.charAt(i+1) == '=') addOperations(tco.createOp(lexemes.charAt(i),lexemes.charAt(++i)), lineToken);
                    else addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                case TokenState.STATE.EQUALS:
                    if (lexemes.charAt(i+1) == '=') addOperations(tco.createOp(lexemes.charAt(i),lexemes.charAt(++i)), lineToken);
                    else addOperations(String.valueOf(lexemes.charAt(i)), lineToken);
                    break;
                case TokenState.STATE.NONE:
                    System.out.println("State is NONE: " + lexemes.charAt(i));
            }
        }
    }
//maam! kuyawan ko kng sayop ako ako gibuhat kay sakto sa ako test pero sayop sa sample TOTTT
//sunod maam taronga imo double ug single quotes TTOTT special characters man na bha, maaan og variable maam TTOTT

    //for identifiers and literals lang ni sha oi
    private void addToken(StringBuilder str, List<Token> lineToken, boolean isEscape){
        // mag himo kog mga exceptions na folder
        // this should return syntax error
//        System.out.println(str);
        if (str.isEmpty() && isEscape) throw new IllegalArgumentException("Syntax Error: Walay sulod ang escape code");
        else if (str.isEmpty()) return;

        String lexeme = str.toString();
        boolean foundType = false;

        switch (lexeme){
            case "UG":
                lineToken.add(new Token(TokenType.LOG_AND, lexeme));
                foundType = true;
                break;
            case "O":
                lineToken.add(new Token(TokenType.LOG_OR, lexeme));
                foundType = true;
                break;
            case "DILI":
                lineToken.add(new Token(TokenType.LOG_NOT, lexeme));
                foundType = true;
                break;
        }

        for (LiteralChecker lc : literalCheckers){
//            System.out.println(lexeme + " -> " + lc.getClass().getSimpleName());
            if (lc.isLiteral(lexeme)){
                lineToken.add(lc.addToken(lexeme));
                foundType = true;
                break;
            }
        }

        if (!foundType) {
            if (isEscape) {
                lineToken.add(new Token(TokenType.ESCAPE_CODE, lexeme));
                str.setLength(0);
                return;
            }
            lineToken.add(new Token(TokenType.VARIABLE, lexeme)); //if variable
            str.setLength(0);
            return;
        }
        str.setLength(0);
    }

    private void addOperations(String token, List<Token> lineToken){
        TokenType type = TokenType.NONE;
        switch (token){
            //arithmetic
            case "+":
                type = TokenType.ARITH_ADD; break;
            case "-":
                type = TokenType.ARITH_MINUS; break;
            case "*":
                type = TokenType.ARITH_MULT; break;
            case "/":
                type = TokenType.ARITH_DIV; break;
            case "%":
                type = TokenType.ARITH_MOD; break;
            case "(":
                type = TokenType.ARITH_OPEN_P; break;
            case ")":
                type = TokenType.ARITH_CLOSE_P; break;

            // relational
            case ">":
                type = TokenType.ARITH_GT; break;
            case ">=":
                type = TokenType.ARITH_GOE; break;
            case "<":
                type = TokenType.ARITH_LT; break;
            case "<=":
                type = TokenType.ARITH_LOE; break;
            case "<>":
                type = TokenType.ARITH_NOT_EQUAL; break;
            case "==":
                type = TokenType.ARITH_EQUAL; break;

            // assignment operation
            case "=":
                type = TokenType.ASS_OP; break;
        }
        if (type == TokenType.NONE) throw new IllegalArgumentException("Not an operator");
        lineToken.add(new Token(type, String.valueOf(token)));
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
            lineToken.add(new Token(TokenType.COLON, lexeme.substring(lexeme.length()-1)));
        }
        else throw new IllegalArgumentException("Sayop: Imo gipasabot ba kay " + (lexeme.contains("IPAKITA") ? "IPAKITA" : "DAWAT") + "?");
    }

}
