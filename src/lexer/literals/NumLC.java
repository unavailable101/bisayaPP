package lexer.literals;

import lexer.TokenType;

public class NumLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
        return false;
    }

    @Override
    public void addToken(TokenType type, String lexeme, int line) {
    }
}
