package lexer;

public enum TokenType {
    KEYWORD,            // : general category
    IDENTIFIER,         // : variable names
    NUMBER,             // : numeric literals
    STRING,             // : string literals
    CHARACTERS,         // : character literals
    OPERATOR,           // : +, -, *, /, %, =, <, >, <=, >=, ==, <>, (, )   -- unya nanang parenthesis
    SYMBOL,             // : special symbol
                        //      - &     : concat strings
                        //      - []    : escape code
                        //      - $     : next line (equivalent to \n)
    BOOLEAN,            // : "OO" or "DILI"
                        //      - must be capital letters
    COMMENT             // : starts with "--"
}
