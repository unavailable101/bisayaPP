package lexer;

//note: walay keyword na PUNDOK, deretso nani kay kapoy

public enum TokenType {
    START_PROG,                  // : start program
    END_PROG,                    // : end program

//    KEYWORD,              // : general category
    VAR_DECLARATION,        // : MUGNA
    DATA_TYPE,              // : data types (NUMERO, LETRA, TINUOD, TIPIK, PISI)
    VARIABLE,               // : variable names
    INTEGER,                // : numbers with no decimal literals
    DOUBLE,                 // : numbers with decimals literals
    STRING,                 // : string literals
    CHARACTERS,             // : character literals

    INPUT,                  // : get input
    OUTPUT,                 // : show output or print

    ASS_OP,                 // : = assignment operator
    ARITH_OP,               // : +, -, *, /, %, <, >, <=, >=, ==, <>, (, )   -- unya nanang parenthesis, apil na dire ang unary operator
    LOG_OP,                 // : UG, O, DILI

//    SYMBOL,               // : special symbol

    CONCAT,                 //      - &     : concat strings
    BRACKET_OPEN,           //      - []    : escape code
    BRACKET_CLOSE,
    NEW_LINE,               //      - $     : next line (equivalent to \n)

    COLON,                  // : colons para sa katu 'IPAKITA' na keyword, i think part man sd na sha sa special symbol

    COMMA,                  // : comma as in (,)

    BOOLEAN,                // : "OO" or "DILI"
                            //      - must be capital letters, enclosed with double quote "

    COMMENT,                // : starts with "--"

    NONE
}
