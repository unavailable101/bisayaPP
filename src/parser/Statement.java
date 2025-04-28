package parser;

import lexer.Token;

import java.util.List;

public abstract class Statement {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        <R> R visitExpr(Expr statement);
        <R> R visitOutput(Output statement);
        <R> R visitInput(Input statement);
        <R> R visitVarDeclaration(VarDeclaration statement);
        <R> R visitIfStatement(IfStatement statement);
        <R> R visitBlockStatement(BlockStatement statement);
        <R> R visitWhileStatement(WhileStatement statement);
        <R> R visitForStatement(ForStatement statement); // para sa ALANG SA
        <R> R visitBreakStatement(BreakStatement statement);
        <R> R visitContinueStatement(ContinueStatement statement);
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

    public static class BlockStatement extends Statement{

        public final List<Statement> statements;
        public BlockStatement(List<Statement> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStatement(this);
        }


    }

    public static class IfStatement extends Statement {
        public final Expression condition;
        public final Statement thenBlock;
        public final List<Expression> elseIfConditions;
        public final List<Statement> elseIfBlocks;
        public final Statement elseBlock;

        public IfStatement(Expression condition, Statement thenBlock, List<Expression> elseIfConditions,
                           List<Statement> elseIfBlocks, Statement elseBlock) {
            this.condition = condition;
            this.thenBlock = thenBlock;
            this.elseIfConditions = elseIfConditions;
            this.elseIfBlocks = elseIfBlocks;
            this.elseBlock = elseBlock;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStatement(this);
        }
    }

    public static class WhileStatement extends Statement{
        public final Expression condition;
        public final Statement thenBlock;

        public WhileStatement(Expression condition, Statement thenBlock) {
            this.condition = condition;
            this.thenBlock = thenBlock;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStatement(this);
        }
    }

    public static class ForStatement extends Statement {
        public final Statement initializer;
        public final Expression condition;
        public final Expression increment;
        public final Statement block;

        public ForStatement(Statement initializer, Expression condition, Expression increment, Statement block) {
            this.initializer = initializer;
            this.condition = condition;
            this.increment = increment;
            this.block = block;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStatement(this);
        }

        public Statement getInitializer() {
            return initializer;
        }
    }
    public static class BreakStatement extends Statement{
        public final Token keyword;

        public BreakStatement(Token keyword) {
            this.keyword=keyword;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStatement(this);
        }
    }

    public static class ContinueStatement extends Statement{
        public final Token keyword;

        public ContinueStatement(Token keyword) {
            this.keyword=keyword;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStatement(this);
        }
    }

}
