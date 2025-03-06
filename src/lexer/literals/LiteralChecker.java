package lexer.literals;

import lexer.Token;
import lexer.TokenType;

public interface LiteralChecker {
    boolean isLiteral(String lexeme);
    Token addToken(String lexeme);

}
