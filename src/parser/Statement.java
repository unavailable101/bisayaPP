package parser;

import java.util.List;

public abstract class Statement {
    // WALAAA NAKOOOOO KAYBAAAAAAAW

    // TABAAAAAAAAAAAAANG

    // < statement >    ->      < expr_statement >
    static class Expr extends Statement {
        final List<Statement> statements;

        Expr(List<Statement> statements){
            this.statements = statements;
        }
    }

    // < statement >            ->  < output_statement >
    // < output_statement >     ->  OUTPUT COLON <  >
    static class Output extends Statement{

    }

    // < statement >            ->  < input_statement >
    // < input_statement >     ->  INPUT COLON IDENTIFIER (COMMA IDENTIFIER)*
    static class Input extends Statement{

    }

}
