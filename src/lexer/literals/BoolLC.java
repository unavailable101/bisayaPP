package lexer.literals;

import lexer.TokenType;

public class BoolLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
        return false;
    }

    @Override
    public void addToken(TokenType type, String lexeme, int line) {
    }
    //both integer and double/decimal ang naa dire
}
