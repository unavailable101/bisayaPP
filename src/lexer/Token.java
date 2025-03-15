package lexer;

public class Token {
    private final TokenType type;
    private final Object value;
    private int line;


    public Token(TokenType type, Object value) {
        this.type = type;
        this.value = value;
        this.line = -1;
    }

    public TokenType getType() {
        return type;
    }

    public Object getValue() {
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
