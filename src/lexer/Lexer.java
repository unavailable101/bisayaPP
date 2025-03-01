package lexer;

import java.util.*;

//basic features sa ta hehe, later na ang conditional and loops
//mas dako trabahoon ang basics

public class Lexer {

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    private static final Map<String, TokenType> IDENTIFIERS = new HashMap<>();      //variables

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

        KEYWORDS.put("UG", TokenType.LOG_OP);
        KEYWORDS.put("O", TokenType.LOG_OP);
        KEYWORDS.put("DILI", TokenType.LOG_OP);

        KEYWORDS.put(null, TokenType.NONE);
    }


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
//        System.out.println(this.lines);
    }

    public List<Token> readlines(){

        for (String line : lines){
            System.out.println("reading line: " + (lines.indexOf(line) + 1));
            tokenizerTRM(line.trim());   //di ko mu basa sa way laman na line
        }
        System.out.println(tokenLines);

        return tokenLines;
    }

    public void tokenizerTRM(String line){          //TRM (Terminal Symbols): Keywords and operators

        // skip comments, expected that a line starts with -- is a comment
        if (line.startsWith("--") || line.isBlank()) return;

        List<String> lexemes = new ArrayList<>(Arrays.asList(line.split("\\s+")));

        for (int i = 0; i< lexemes.size(); i++){
            String lexeme = lexemes.get(i);
            Object type = KEYWORDS.get(lexeme);  // ma return shag null kng wala sa hashmap, di dawaton ni default ang null, mag error, need sha i try-catch here
            if (type != null) {
                switch (KEYWORDS.get(lexeme)) {
                    case TokenType.START_PROG:
                        tokenLines.add(new Token(TokenType.START_PROG, lexeme)); break;
                    case TokenType.VAR_DECLARATION:
                        tokenLines.add(new Token(TokenType.VAR_DECLARATION, lexeme)); break;
                    case TokenType.DATA_TYPE:
                        tokenLines.add(new Token(TokenType.DATA_TYPE, lexeme)); break;
                    case TokenType.LOG_OP:
                        tokenLines.add(new Token(TokenType.LOG_OP, lexeme)); break;
                    case TokenType.INPUT:
                        tokenLines.add(new Token(TokenType.INPUT, lexeme)); break;
                    case TokenType.OUTPUT:
                        tokenLines.add(new Token(TokenType.OUTPUT, lexeme)); break;
                    case TokenType.END_PROG:
                        tokenLines.add(new Token(TokenType.END_PROG, lexeme)); break;
                }
            }
            else if (lexeme.contains("IPAKITA") || lexeme.contains("DAWAT")) checkIO(lexeme);
            else if (lexeme.matches(":")) tokenLines.add(new Token(TokenType.COLON, lexeme));
//            else if (lexeme.matches("^[a-zA-Z][a-zA-Z0-9_\\-+*=&^%$#@!?~`|\\\\/<>,.;()\\[\\]]*$")) {
            else if (lexeme.matches("^[a-zA-Z][a-zA-Z0-9\\W_]*$")) {
//                 convert to string kay mag nested for loop ko if dili, kapoy nang nested for loop oi, nya ang kuan sd ana, time complexity
                tokenizeParts(String.join(" ",lexemes.subList(lexemes.indexOf(lexeme), lexemes.size())));
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
    private void tokenizeParts(String lexemes){

        StringBuilder buffer = new StringBuilder();

        // skip delimiters ' '
        for (int i = 0; i<lexemes.length() && lexemes.charAt(i) != ' '; i++){
            switch (lexemes.charAt(i)){
                case ',':
                    addToken(buffer);
                    tokenLines.add(new Token(TokenType.COMMA, String.valueOf(lexemes.charAt(i)))); break;
                case '=':
                    addToken(buffer);
                    tokenLines.add(new Token(TokenType.ASS_OP, String.valueOf(lexemes.charAt(i)))); break;
                case '+':
                case '-':
                case '*':
                case '/':
                case '%':
                    addToken(buffer);
                    tokenLines.add(new Token(TokenType.ARITH_OP, String.valueOf(lexemes.charAt(i)))); break;
                case '$':
                    addToken(buffer);
                    tokenLines.add(new Token(TokenType.NEW_LINE, String.valueOf(lexemes.charAt(i)))); break;
                case '&':
                    addToken(buffer);
                    tokenLines.add(new Token(TokenType.CONCAT, String.valueOf((lexemes.charAt(i))))); break;
                    // diko kaybaw unsaon ang [], i consider ang naa sa sulod sa brackets, therefore unya nani hehe
                default:
                    buffer.append(lexemes.charAt(i));

            }
        }
        System.out.println();
    }

    private void addToken(StringBuilder buff){
        // katugon nako
    }

    /*
        para sa mga i/o tungod naay cases na instead of butang space between colon and keyword, magka duol sila
            Example:
                IPAKITA:        -- instead of IPAKITA :
                DAWAT:

        the rest need gyud silag space between
    */
    private void checkIO(String lexeme){
        if (lexeme.endsWith(":")) {
            tokenLines.add(new Token(KEYWORDS.get(lexeme.contains("IPAKITA") || lexeme.contains("DAWAT")), lexeme.substring(0, lexeme.length() - 1)));
            tokenLines.add(new Token(TokenType.COLON, lexeme.substring(lexeme.length()-1)));
        }
        else throw new IllegalArgumentException("Sayop: Imo gipasabot ba kay " + (lexeme.contains("IPAKITA") ? "IPAKITA" : "DAWAT") + "?");
    }

}
