package lexer;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

//    for checking lng ni to see sakto bha
    @Override
    public String toString() {
        return "Tokens{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
