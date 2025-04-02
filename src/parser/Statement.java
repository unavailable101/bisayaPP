package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.List;

public abstract class Statement {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        <R> R visitExpr(Expr statement);
        <R> R visitOutput(Output statement);
        <R> R visitInput(Input statement);
        <R> R visitVarDeclaration(VarDeclaration statement);
    }
    // < statement >    ->      < expr_statement >
    public static class Expr extends Statement {
        public final Expression expression;

        Expr(Expression expression){
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpr(this);
        }
    }

    // < statement >            ->  < output_statement >
    // < output_statement >     ->  OUTPUT COLON < expression >
    public static class Output extends Statement{
        public final Expression expression;
        public Output(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitOutput(this);
        }
    }

    // < statement >            ->  < input_statement >
    // < input_statement >     ->  INPUT COLON IDENTIFIER (COMMA IDENTIFIER)*
    public static class Input extends Statement{
        public final Token variable;
        public Input (Token variable) {
            this.variable = variable;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitInput(this);
        }
    }

    public static class VarDeclaration extends Statement{
        public final Token type;
        public final Token var;
        public final Expression initialization;

        VarDeclaration(Token type, Token var, Expression initialization) {
            this.type = type;
            this.var = var;
            this.initialization = initialization;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarDeclaration(this);
        }
    }

}
