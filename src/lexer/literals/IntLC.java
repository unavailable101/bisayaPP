package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class IntLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
        return lexeme.matches("\\d+");
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.INTEGER, lexeme);
    }
}
