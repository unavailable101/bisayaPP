package parser;

import lexer.Token;

import java.util.List;

public abstract class Statement {

    // < statement >    ->      < expr_statement >
    static class Expr extends Statement {
        final Expression expression;

        Expr(Expression expression){
            this.expression = expression;
        }
    }

    // < statement >            ->  < output_statement >
    // < output_statement >     ->  OUTPUT COLON < expression >
    static class Output extends Statement{
        final Expression expression;
        public Output(Expression expression) {
            this.expression = expression;
        }
    }

    // < statement >            ->  < input_statement >
    // < input_statement >     ->  INPUT COLON IDENTIFIER (COMMA IDENTIFIER)*
    static class Input extends Statement{
        final Expression variables;
        public Input (Expression variables) {
            this.variables = variables;
        }
    }

    static class VarDeclaration extends Statement{
        final Token type;
        final Expression initialization;
//        final Variable var;

        VarDeclaration(Token type, Expression initialization) {
            this.type = type;
            this.initialization = initialization;
//            this.var = var;
        }
    }

}
