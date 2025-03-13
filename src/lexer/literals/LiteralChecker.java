package lexer.literals;

import lexer.Token;
import lexer.TokenType;

//under this package kay dapat ang nay access ragyud kay under sa lexer na package
//di dapat ma gamit sa other packages (parser and interpreter)
public interface LiteralChecker {
    boolean isLiteral(String lexeme);
    Token addToken(String lexeme);

}
