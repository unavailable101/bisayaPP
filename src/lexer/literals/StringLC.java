package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class StringLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
//        return lexeme.startsWith("\"") && lexeme.endsWith("\"");
        if (lexeme.startsWith("\"")){
            if (!lexeme.endsWith("\"")) throw new IllegalArgumentException("Sayop: kulangan ni og \"");
        } else return false;
        return true;
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.STRING, lexeme);
    }
}
