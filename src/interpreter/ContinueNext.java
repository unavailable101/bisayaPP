package interpreter;

import lexer.Token;

public class ContinueNext extends RuntimeException{
    Token token;
    ContinueNext(Token token){
        super(null, null, false, false);
        this.token = token;
    }

    @Override
    public String toString() {
        return "";  // Return empty string instead of null
    }

    @Override
    public String getMessage() {
        return "";  // Return empty string for getMessage()
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
