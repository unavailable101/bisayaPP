package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

// hello tree, my worst nightmare
// we meet again
// akala ko graduate na ako sayo, pero hindi pa pala

/*
    grammar

    < program >             ->      START_PROG < statements > END_PROG

    < statements >          ->      < statement > | < statements >
    -- each line is a statement already, so mahug automatic shag new line

    < statement >           ->      < output_statement >  |
                                    < input_statement >   |
                                    < var_declare >

    < expr_statement >      ->      < var_declare >     |
                                    < assign >          |

    < var_declare >         ->      VAR_DECLARATION < data_type >  < var >

    < data_type >           ->      "NUMERO" | "lETRA" | "TIPIK" | "TINUOD" | "PISI"

    < var >                 ->      IDENTIFIER  |
                                    < assign >  |
                                    COMMA < var >

    < assign >              ->      IDENTIFIER ASSIGN_OP ( < assign > | < expression >)

    < expression >          ->      ( < literal > | IDENTIFIER )    |
                                    < unary >                       |
                                    < binary_operation >            |
                                    < group_expression >

    < literal >             ->      INTEGER | DOUBLE | STRING | CHARACTERS | < boolean >

    < boolean >             ->      "OO" | "DILI"

    < unary >               ->      ( ARITH_ADD | ARITH_MINUS ) ( literal | IDENTIFIER )

    < binary_operation >    ->      < expression > < arith_operator > < expression >

    < arith_operators >     ->      ADD | MINUS | MULT | DIV | MOD | GT | LT | GOE | LOE | EQUAL | NOT_EQUAL

    < group_expression >    ->      OPEN_P < expression > CLOSE_P

    < output_statement >    ->      OUTPUT COLON <  >

    < input_statement >     ->      INPUT COLON IDENTIFIER (COMMA IDENTIFIER)*

*/

public class Parser {
    //naa na dire ang laay
    //kapoy
    private final List<List<Token>> lineTokens;
    private int line;

    public Parser(List<List<Token>> lineTokens) {
        this.lineTokens = lineTokens;
        this.line = -1;
    }

    public List<Statement> parse(){

        int size = 0;

        if (lineTokens.getFirst().getFirst() != lineTokens.getFirst().getLast() &&
                lineTokens.getFirst().getFirst().getType() != TokenType.START_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'SUGOD' before line " + lineTokens.getFirst().getFirst().getLine());
        else ++line;


        if (lineTokens.getLast().getFirst() != lineTokens.getLast().getLast() &&
                lineTokens.getLast().getLast().getType() != TokenType.END_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'KATAPUSAN' after line " + lineTokens.getLast().getLast().getLine());
        else size = lineTokens.size()-1;

        //dire rako kutob eh
        List<Statement> statements = new ArrayList<>();
        List<Token> tokens;

        for (int i = 1; i < size; i++) {
            tokens = nextLine();
            statements.add(statement(tokens));
        }

        return statements;
    }

    private Statement statement(List<Token> tokens){
        Statement st = null;

        // ambot na oi

        return st;
    }

    List<Token> nextLine(){
        return lineTokens.get(++line);
    }

}
