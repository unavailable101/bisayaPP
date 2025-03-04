package lexer;

public class Token {
    private final TokenType type;
    private final String value;
    private int line;


    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.line = -1;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line){
        this.line = line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", line=" + line +
                '}';
    }
}
