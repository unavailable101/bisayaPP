package parser;

import lexer.Token;

import java.util.List;

public abstract class Statement {

    abstract <R> R accept (Visitor<R> visitor);

    interface Visitor<R> {
        <R> R visitExpr(Expr statement);
        <R> R visitOutput(Output statement);
        <R> R visitInput(Input statement);
        <R> R visitVarDeclaration(VarDeclaration statement);
    }
    // < statement >    ->      < expr_statement >
    static class Expr extends Statement {
        final Expression expression;

        Expr(Expression expression){
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpr(this);
        }
    }

    // < statement >            ->  < output_statement >
    // < output_statement >     ->  OUTPUT COLON < expression >
    static class Output extends Statement{
        final Expression expression;
        public Output(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitOutput(this);
        }
    }

    // < statement >            ->  < input_statement >
    // < input_statement >     ->  INPUT COLON IDENTIFIER (COMMA IDENTIFIER)*
    static class Input extends Statement{
        final Token variable;
        public Input (Token variable) {
            this.variable = variable;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInput(this);
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

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarDeclaration(this);
        }
    }

}
