package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class BoolLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {   //Accepted values: "OO" and "DILI"
        return lexeme.matches("^\"(OO|DILI)\"$");
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.BOOLEAN, lexeme);
    }
}
