package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public class BoolLC implements LiteralChecker{
    @Override
    public boolean isLiteral(String lexeme){   //Accepted values: "OO" and "DILI"
        return lexeme.matches("^\"(OO|DILI)\"$");
//        if (lexeme.startsWith("\"")){
//            if (!lexeme.endsWith("\"")) throw new IllegalArgumentException("Sayop: kulangan ni og \"");
//        }
//        return lexeme.substring(1,lexeme.length()-1).equals("OO") || lexeme.substring(1,lexeme.length()-1).equals("DILI");
    }

    @Override
    public Token addToken(String lexeme) {
        return new Token(TokenType.BOOLEAN, lexeme);
    }
}
