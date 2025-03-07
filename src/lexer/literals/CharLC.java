package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class CharLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme) {
//        return lexeme.startsWith("'") && lexeme.endsWith("'") && lexeme.length()==3;
//        System.out.println("lexeme: " + lexeme + " length: " + lexeme.length());
        if (lexeme.startsWith("'")){
            if (!lexeme.endsWith("'")) throw new IllegalArgumentException("Sayop: kulangan ni og '");
            if (lexeme.length() > 3) throw new IllegalArgumentException("Sayop: dili na ni siya character");
        } else return false;
        return true;
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.CHARACTERS, lexeme);
    }
}
