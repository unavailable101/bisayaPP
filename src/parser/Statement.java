package parser;

import lexer.Token;

import java.util.List;

public abstract class Statement {

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
        public Output() {
        }
    }

    // < statement >            ->  < input_statement >
    // < input_statement >     ->  INPUT COLON IDENTIFIER (COMMA IDENTIFIER)*
    static class Input extends Statement{
        public Input() {
        }
    }

    static class VarDeclaration extends Statement{
        final Token type;
//        final Variable var;

        VarDeclaration(Token type) {
            this.type = type;
//            this.var = var;
        }
    }

}
