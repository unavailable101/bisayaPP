package lexer;

import lexer.literals.*;

import java.util.*;

//basic features sa ta hehe, later na ang conditional and loops
//mas dako trabahoon ang basics

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

                tokenizerTRM(line, lineToken);
                for (Token t : lineToken) t.setLine(i+1);
//                System.out.println(lineToken);
                if (!lineToken.isEmpty()) tokenLines.add(lineToken);
            }
        }
        for (List<Token> tl : tokenLines) System.out.println(tl);
        return tokenLines;
    }

    private void tokenizerTRM(String line, List<Token> lineToken){          //TRM (Terminal Symbols): Keywords and operators

        // skip comments, expected that a line starts with -- is a comment
        if (line.startsWith("--") || line.isBlank()) return;

        List<String> lexemes = new ArrayList<>(Arrays.asList(line.split("\\s+")));

        for (int i = 0; i< lexemes.size(); i++){
            String lexeme = lexemes.get(i);
            Object type = KEYWORDS.get(lexeme);  // ma return shag null kng wala sa hashmap, di dawaton ni default ang null, mag error, need sha i try-catch here
            if (type != null) {
                switch (KEYWORDS.get(lexeme)) {
                    case TokenType.START_PROG:
                        lineToken.add(new Token(TokenType.START_PROG, lexeme)); break;
                    case TokenType.VAR_DECLARATION:
                        lineToken.add(new Token(TokenType.VAR_DECLARATION, lexeme)); break;
                    case TokenType.DATA_TYPE:
                        lineToken.add(new Token(TokenType.DATA_TYPE, lexeme)); break;
                    case TokenType.LOG_AND:
                        lineToken.add(new Token(TokenType.LOG_AND, lexeme)); break;
                    case TokenType.LOG_OR:
                        lineToken.add(new Token(TokenType.LOG_OR, lexeme)); break;
                    case TokenType.LOG_NOT:
                        lineToken.add(new Token(TokenType.LOG_NOT, lexeme)); break;
                    case TokenType.INPUT:
                        lineToken.add(new Token(TokenType.INPUT, lexeme)); break;
                    case TokenType.OUTPUT:
                        lineToken.add(new Token(TokenType.OUTPUT, lexeme)); break;
                    case TokenType.END_PROG:
                        lineToken.add(new Token(TokenType.END_PROG, lexeme)); break;
                }
            }
            else if (lexeme.contains("IPAKITA") || lexeme.contains("DAWAT")) checkIO(lexeme, lineToken);
            else if (lexeme.matches(":")) lineToken.add(new Token(TokenType.COLON, lexeme));
//            else if (lexeme.matches("^[a-zA-Z][a-zA-Z0-9_\\-+*=&^%$#@!?~`|\\\\/<>,.;()\\[\\]]*$")) {
//            else if (lexeme.matches("^[a-zA-Z][a-zA-Z0-9\\W_]*$")) {
            else if (lexeme.matches("^[\\s\\S]*$"))  {
//                 convert to string kay mag nested for loop ko if dili, kapoy nang nested for loop oi, nya ang kuan sd ana, time complexity
                tokenizeParts(String.join(" ",lexemes.subList(lexemes.indexOf(lexeme), lexemes.size())), lineToken);
                break;
            }
            else System.out.println("Unidentified Token: " + lexeme);
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

        for (int i = 0; i<lexemes.length(); i++){

            if (isChar){
                isChar = false;
                lexeme.append(lexemes.charAt(i));
                lexeme.append(lexemes.charAt(i+1));
                addToken(lexeme, lineToken, isEscape);
                ++i;
                lexeme = new StringBuilder();
                continue;
            }

            if (isString){
                lexeme.append(lexemes.charAt(i));
                if (lexemes.charAt(i) == '"'){
                    isString = false;
                    addToken(lexeme, lineToken, isEscape);
                    lexeme = new StringBuilder();
                }
                continue;
            }

            // skip delimiters ' ' or spaces
            if (lexemes.charAt(i) == ' ' && !isString) {
                addToken(lexeme, lineToken, isEscape);
                lexeme = new StringBuilder();
                continue;
            }

            //teka lang, daghan kog dapat i consider kng mag string ko aty samoka
            //si string ang medjo ano, basta TTOTT

            switch (lexemes.charAt(i)){
                case '"':
                    if (!isString){
                        lexeme.append(lexemes.charAt(i));
                        isString = true;
                    } else {
                        lexeme.append(lexemes.charAt(i));
                        addToken(lexeme, lineToken, isEscape);
                        isString = false;
                    }
                    break;
                case '\'':
                    if (!isChar){
                        lexeme.append(lexemes.charAt(i));
                        isChar = true;
                    }
                    break;
                case ',':
                     addToken(lexeme, lineToken, isEscape);
                        lineToken.add(new Token(TokenType.COMMA, String.valueOf(lexemes.charAt(i))));
                case '=':
                    addToken(lexeme, lineToken, isEscape);
                    if (lexemes.charAt(i+1) == '='){
                        lineToken.add(new Token(TokenType.ARITH_EQUAL, lexemes.substring(i, i+2)));
                        ++i;
                    } else lineToken.add(new Token(TokenType.ASS_OP, String.valueOf(lexemes.charAt(i))));
                    break;
                case '(':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.ARITH_OPEN_P, String.valueOf(lexemes.charAt(i)))); break;
                case ')':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.ARITH_CLOSE_P, String.valueOf(lexemes.charAt(i)))); break;
                case '+':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.ARITH_ADD, String.valueOf(lexemes.charAt(i)))); break;
                case '*':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.ARITH_MULT, String.valueOf(lexemes.charAt(i)))); break;
                case '/':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.ARITH_DIV, String.valueOf(lexemes.charAt(i)))); break;
                case '%':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.ARITH_MOD, String.valueOf(lexemes.charAt(i)))); break;
                case '-':
                    if (lexemes.charAt(i+1) == '-'){    // why tf you wont print nor add in the lineToken you motherfucker  // the fuck ganeha ra diay ni ni gana
                        //so ok na ang inline comments
                        //from this char to the last char at this current line kay i store as comment, mag substring ko ani? then set i to length-1 or better i break
                        lineToken.add(new Token(TokenType.COMMENT, lexemes.substring(i)));
                        return;
                    } else {
                        addToken(lexeme, lineToken, isEscape);
                        lineToken.add(new Token(TokenType.ARITH_MINUS, String.valueOf(lexemes.charAt(i))));
                    }
                    break;
                case '<':
                    addToken(lexeme, lineToken, isEscape);
                    if (lexemes.charAt(i+1) == '>') {
                        lineToken.add(new Token(TokenType.ARITH_NOT_EQUAL, lexemes.substring(i, i + 2)));
                        ++i;
                    } else if (lexemes.charAt(i+1) == '=') {
                        lineToken.add(new Token(TokenType.ARITH_GOE, lexemes.substring(i, i + 2)));
                        ++i;
                    } else lineToken.add(new Token(TokenType.ARITH_GT, String.valueOf(lexemes.charAt(i))));
                    break;
                case '>':
                    addToken(lexeme, lineToken, isEscape);
                    if (lexemes.charAt(i+1) == '=') {
                        lineToken.add(new Token(TokenType.ARITH_LOE, lexemes.substring(i, i + 2)));
                        ++i;
                    } else lineToken.add(new Token(TokenType.ARITH_LT, String.valueOf(lexemes.charAt(i))));
                    break;
                case '$':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.NEW_LINE, String.valueOf(lexemes.charAt(i)))); break;
                case '&':
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.CONCAT, String.valueOf((lexemes.charAt(i))))); break;
                    //wa bitaw ko kasabot about aning escape code, ill ask unsa gyud ni sha
                case '[':
                    if (isEscape) throw new IllegalArgumentException("Sayop: Wala nimo gi close ang bracket");
                    isEscape = true;
                    lineToken.add(new Token(TokenType.BRACKET_OPEN, String.valueOf(lexemes.charAt(i))));
                    break;
                case ']':
                    if (!isEscape) throw new IllegalArgumentException("Sayop: Wala nimo gi close ang bracket");
                    addToken(lexeme, lineToken, isEscape);
                    lineToken.add(new Token(TokenType.BRACKET_CLOSE, String.valueOf(lexemes.charAt(i))));
                    isEscape = false;
                    break;
                default:
//                    System.out.println("Unidentified token part: " + lexemes.charAt(i));
                    lexeme.append(lexemes.charAt(i));
//                    System.out.println(lexeme);
            }
        }
//            System.out.println();
        addToken(lexeme, lineToken, isEscape);
    }
//maam! kuyawan ko kng sayop ako ako gibuhat kay sakto sa ako test pero sayop sa sample TOTTT
//sunod maam taronga imo double ug single quotes TTOTT special characters man na bha, maaan og variable maam TTOTT

    private void addToken(StringBuilder str, List<Token> lineToken, boolean isEscape){
        // mag himo kog mga exceptions na folder
        // this should return syntax error
//        System.out.println(str);
        if (str.isEmpty() && isEscape) throw new IllegalArgumentException("Syntax Error: Walay sulod ang escape code");
        else if (str.isEmpty()) return;

        String lexeme = str.toString();
        boolean foundType = false;

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
