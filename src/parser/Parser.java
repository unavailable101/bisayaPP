package parser;

import lexer.Token;

import java.util.List;

/*
    grammar

    < program >             ->      "SUGOD" < statements > "KATAPUSAN"

    < statements >          ->      < statement > | < statements >          -- each line is a statement already

    < statement >           ->      < output_statement >  |
                                    < input_statement >   |
                                    < expr_statement >

    < expr_statement >      ->      < var_declare >     |
                                    < assign >          |

    < var_declare >         ->      "MUGNA" < data_type >  < var >

    < data_type >           ->      "NUMERO"| "lETRA" | "TIPIK" | "TINUOD" | "PISI"

    < var >                 ->      IDENTIFIER  |
                                    < assign >  |
                                    COMMA < var >

    < assign >              ->      IDENTIFIER ASSIGN_OP ( < assign > | LITERALS )

    < output_statement >    ->      "IPAKITA" COLON <  >

    < input_statement >     ->      "DAWAT" COLON IDENTIFIER (COMMA IDENTIFIER)*

*/

public class Parser {
    //naa na dire ang laay
    //kapoy
    private List<List<Token>> tokens;
    private int start;

    public Parser(List<List<Token>> tokens) {
        this.tokens = tokens;
        this.start = -1;
    }



}
