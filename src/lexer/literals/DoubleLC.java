package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class DoubleLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
        return lexeme.matches("\\d*\\.\\d+");
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.DOUBLE, lexeme);
    }
}
