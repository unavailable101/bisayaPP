package lexer;

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

        KEYWORDS.put("UG", TokenType.LOG_OP);
        KEYWORDS.put("O", TokenType.LOG_OP);
        KEYWORDS.put("DILI", TokenType.LOG_OP);

        KEYWORDS.put(null, TokenType.NONE);     // wa man ni gamit oi, di man ni ma recognize
    }


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

            System.out.println("Reading line: " + (i+1));

            String line = lines.get(i).trim();

            if(!line.isEmpty()){

                List<Token> lineToken = new ArrayList<>();

                tokenizerTRM(line, lineToken, i+1);
//                System.out.println(lineToken);
                if (!lineToken.isEmpty()) tokenLines.add(lineToken);
            }
        }
//        for (String line : lines){
//            System.out.println("reading line: " + (lines.indexOf(line) + 1));
//            tokenizerTRM(line.trim());   //di ko mu basa sa way laman na line     //botbot, basahon japun niya gwapa, i skip nga lang
//        }
//        System.out.println(tokenLine);
//        tokenLines.add(tokenLine);
//        tokenLine.clear();
        for (List<Token> tl : tokenLines) System.out.println(tl);
        return tokenLines;
    }

    public void tokenizerTRM(String line, List<Token> lineToken, int currLine){          //TRM (Terminal Symbols): Keywords and operators

        // skip comments, expected that a line starts with -- is a comment
        if (line.startsWith("--") || line.isBlank()) return;

        List<String> lexemes = new ArrayList<>(Arrays.asList(line.split("\\s+")));

        for (int i = 0; i< lexemes.size(); i++){
            String lexeme = lexemes.get(i);
            Object type = KEYWORDS.get(lexeme);  // ma return shag null kng wala sa hashmap, di dawaton ni default ang null, mag error, need sha i try-catch here
            if (type != null) {
                switch (KEYWORDS.get(lexeme)) {
                    case TokenType.START_PROG:
                        lineToken.add(new Token(TokenType.START_PROG, lexeme, currLine)); break;
                    case TokenType.VAR_DECLARATION:
                        lineToken.add(new Token(TokenType.VAR_DECLARATION, lexeme, currLine)); break;
                    case TokenType.DATA_TYPE:
                        lineToken.add(new Token(TokenType.DATA_TYPE, lexeme, currLine)); break;
                    case TokenType.LOG_OP:
                        lineToken.add(new Token(TokenType.LOG_OP, lexeme, currLine)); break;
                    case TokenType.INPUT:
                        lineToken.add(new Token(TokenType.INPUT, lexeme, currLine)); break;
                    case TokenType.OUTPUT:
                        lineToken.add(new Token(TokenType.OUTPUT, lexeme, currLine)); break;
                    case TokenType.END_PROG:
                        lineToken.add(new Token(TokenType.END_PROG, lexeme, currLine)); break;
                }
            }
            else if (lexeme.contains("IPAKITA") || lexeme.contains("DAWAT")) checkIO(lexeme, lineToken, currLine);
            else if (lexeme.matches(":")) lineToken.add(new Token(TokenType.COLON, lexeme, currLine));
//            else if (lexeme.matches("^[a-zA-Z][a-zA-Z0-9_\\-+*=&^%$#@!?~`|\\\\/<>,.;()\\[\\]]*$")) {
            else if (lexeme.matches("^[a-zA-Z][a-zA-Z0-9\\W_]*$")) {
//                 convert to string kay mag nested for loop ko if dili, kapoy nang nested for loop oi, nya ang kuan sd ana, time complexity
                tokenizeParts(String.join(" ",lexemes.subList(lexemes.indexOf(lexeme), lexemes.size())), lineToken,  currLine);
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
    private void tokenizeParts(String lexemes, List<Token> lineToken, int currLine){

        StringBuilder lexeme = new StringBuilder();

        for (int i = 0; i<lexemes.length(); i++){

            // skip delimiters ' ' or spaces
            if (lexemes.charAt(i) == ' ') {
                addToken(lexeme);
                lexeme = new StringBuilder();
                continue;
            }

            switch (lexemes.charAt(i)){
                case ',':
                    addToken(lexeme);
                    lineToken.add(new Token(TokenType.COMMA, String.valueOf(lexemes.charAt(i)), currLine)); break;
                case '=':
                    addToken(lexeme);
                    lineToken.add(new Token(TokenType.ASS_OP, String.valueOf(lexemes.charAt(i)), currLine)); break;
                case '+':
                case '*':
                case '/':
                case '%':
                    addToken(lexeme);
                    lineToken.add(new Token(TokenType.ARITH_OP, String.valueOf(lexemes.charAt(i)), currLine)); break;
                case '-':
                    if (lexemes.charAt(i+1) == '-'){    // why tf you wont print nor add in the lineToken you motherfucker  // the fuck ganeha ra diay ni ni gana
                        //so ok na ang inline comments
                        //from this char to the last char at this current line kay i store as comment, mag substring ko ani? then set i to length-1 or better i break
                        lineToken.add(new Token(TokenType.COMMENT, lexemes.substring(i), currLine));
                        return;
                    } else {
                        addToken(lexeme);
                        lineToken.add(new Token(TokenType.ARITH_OP, String.valueOf(lexemes.charAt(i)), currLine));
                    }
                    break;
                case '$':
                    addToken(lexeme);
                    lineToken.add(new Token(TokenType.NEW_LINE, String.valueOf(lexemes.charAt(i)), currLine)); break;
                case '&':
                    addToken(lexeme);
                    lineToken.add(new Token(TokenType.CONCAT, String.valueOf((lexemes.charAt(i))), currLine)); break;
                    // diko kaybaw unsaon ang [], i consider ang naa sa sulod sa brackets, therefore unya nani hehe
                default:
                    System.out.println("Unidentified token part: " + lexemes.charAt(i));
                    lexeme.append(lexemes.charAt(i));
            }
        }
            System.out.println();
    }

    private void addToken(StringBuilder str){
        // katugon nako
        String lexeme = str.toString();

        str.setLength(0);
    }

    /*
        para sa mga i/o tungod naay cases na instead of butang space between colon and keyword, magka duol sila
            Example:
                IPAKITA:        -- instead of IPAKITA :
                DAWAT:

        the rest need gyud silag space between
    */
    private void checkIO(String lexeme, List<Token> lineToken, int currLine){
        if (lexeme.endsWith(":")) {
            lineToken.add(new Token(KEYWORDS.get(lexeme.contains("IPAKITA") ? "IPAKITA" : "DAWAT"), lexeme.substring(0, lexeme.length() - 1), currLine));
            lineToken.add(new Token(TokenType.COLON, lexeme.substring(lexeme.length()-1), currLine));
        }
        else throw new IllegalArgumentException("Sayop: Imo gipasabot ba kay " + (lexeme.contains("IPAKITA") ? "IPAKITA" : "DAWAT") + "?");
    }

}
