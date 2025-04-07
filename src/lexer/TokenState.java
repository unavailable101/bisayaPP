package lexer;

//dapat within the package lexer ni, di pwede ma access sa other packages (parser and interpreter)
class TokenState {
    public enum STATE {

        // CHARACTERS OR NUMBERS
        UPPER_CASE,
        LOWER_CASE,
        UNDERSCORE,
        NUMBER,

        // SPECIAL CHARACTERS
        COLON,
        COMMA,
        CONCAT,
        NEW_LINE,
        BRACKET,         //open brackets
        SINGLE_Q,
        DOUBLE_Q,
        BRACES_OPEN,
        BRACES_CLOSE,

        // OPERATIONS
        MINUS,            // :  -   -- either - (arith_op) or --(comment)
        LT,               // : <    -- possible na <, <=, <>
        GT,               // : >    -- possible > or >=
        EQUALS,           // : =    -- either = (assignment) or equals (==)
        ARITH_OP,         // : +, *, /, %     -- part man si - pero mas maayo mag lahi sha

        WHITESPACE,
        NONE
    }

    public STATE tokenState(char c){
        STATE state = STATE.NONE;

        if (Character.isLetter(c)){
            if (Character.isUpperCase(c)) state = STATE.UPPER_CASE;
            if (Character.isLowerCase(c)) state = STATE.LOWER_CASE;
        } else {
            switch (c) {
                case '{':
                    state = STATE.BRACES_OPEN; break;
                case '}':
                    state = STATE.BRACES_CLOSE; break;
                case ':':
                    state = STATE.COLON;
                    break;
                case '\'':
                    state = STATE.SINGLE_Q;
                    break;
                case '"':
                    state = STATE.DOUBLE_Q;
                    break;
                case ',':
                    state = STATE.COMMA;
                    break;
                case '&':
                    state = STATE.CONCAT;
                    break;
                case '$':
                    state = STATE.NEW_LINE;
                    break;
                case '[':
                    state = STATE.BRACKET;
                    break;
                case '-':
                    state = STATE.MINUS;
                    break;
                case '<':
                    state = STATE.GT;
                    break;
                case '>':
                    state = STATE.LT;
                    break;
                case '=':
                    state = STATE.EQUALS;
                    break;
                case '+':
                case '*':
                case '/':
                case '%':
                case '(':
                case ')':
                    state = STATE.ARITH_OP;
                    break;
                case '_':
                    state = STATE.UNDERSCORE;
                    break;
                case ' ':
                case '\n':
                case '\t':
                    state = STATE.WHITESPACE; break;
                default:
                    if (Character.isDigit(c)) state = STATE.NUMBER;
            }
        }
        return state;
    }
}