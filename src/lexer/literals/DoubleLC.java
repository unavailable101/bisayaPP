package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class DoubleLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
        if (!lexeme.matches(".*\\d.*")) return false;
        if (lexeme.chars().filter(ch -> ch == '.').count() > 1) throw new IllegalArgumentException("Invalid decimal literal: Too many decimal points");
        if (!lexeme.matches("\\d*\\.\\d+")) return false;
        return true;
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.DOUBLE, Double.valueOf(lexeme));
    }
}
