package lexer;

//note: walay keyword na PUNDOK, deretso nani kay kapoy

public enum TokenType {
    START_PROG,                  // : start program
    END_PROG,                    // : end program

//    KEYWORD,              // : general category
    VAR_DECLARATION,        // : MUGNA
    DATA_TYPE,              // : data types (NUMERO, LETRA, TINUOD, TIPIK, PISI)
    VARIABLE,               // : variable names

    //was thinking na i generalize lng but murag mag lisod sa parser
//    LITERALS,               // : kani mga integer, double, string, characters... interpreter nay bahala kng sakto bha na literal iya gi gamit para sa specific na data type.. letse nalang
    INTEGER,                // : numbers with no decimal literals
    DOUBLE,                 // : numbers with decimals literals
    STRING,                 // : string literals
    CHARACTERS,             // : character literals
    BOOLEAN,                // : "OO" or "DILI"
                            //      - must be capital letters, enclosed with double quote "

    INPUT,                  // : get input
    OUTPUT,                 // : show output or print

    ASS_OP,                 // : = assignment operator
    ARITH_OP,               // : arithmetic operations: +, -, *, /, %, <, >, <=, >=, ==, <>, (, )   -- unya nanang parenthesis, apil na dire ang unary operator
    LOG_OP,                 // : logical operations: UG, O, DILI

//    SYMBOL,               // : special symbol

    CONCAT,                 //      - &     : concat strings
    BRACKET_OPEN,           //      - []    : escape code
    BRACKET_CLOSE,
    NEW_LINE,               //      - $     : next line (equivalent to \n)

    COLON,                  // : colons para sa katu 'IPAKITA' na keyword, i think part man sd na sha sa special symbol

    COMMA,                  // : comma as in (,)

    COMMENT,                // : starts with "--"

    NONE
}
