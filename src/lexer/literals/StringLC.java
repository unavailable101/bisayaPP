package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class StringLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
        return lexeme.startsWith("\"") && lexeme.endsWith("\"");
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.STRING, lexeme);
    }
}
