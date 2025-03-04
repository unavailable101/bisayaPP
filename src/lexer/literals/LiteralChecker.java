package lexer.literals;

import lexer.TokenType;

public interface LiteralChecker {
    boolean isLiteral(String lexeme);
    void addToken(TokenType type, String lexeme, int line);

}
