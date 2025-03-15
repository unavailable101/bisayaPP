package parser;

import lexer.Token;

public abstract class Expression {

    abstract <R> R accept (Visitor<R> visitor);

    interface Visitor<R>{
        R visitUnary(Unary expression);
        R visitBinary(Binary expression);
        R visitLogic(Logic expression);
        R visitGroup(Group expression);
        R visitLiteral(Literal expression);
        R visitAssign(Assign expression);
        R visitVariable(Variable expression);
    }
    // < expression >   -> < unaru >
    // < unary >        -> ( ADD | MINUS) ( < literal > | IDENTIFIER )
    static class Unary extends Expression{
        final Token op;
        final Expression right;

        public Unary(Token op, Expression right) {
            this.op = op;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary(this);
        }
    }

    // < expression >           -> < binary_operation >
    // < binary_operation >     -> < expression > < operator >  < expression >
    // < operator >             -> ADD | MINUS | MULT | DIV | MOD | GT | LT | GOE | LOE | EQUAL | NOT_EQUAL
    static class Binary extends Expression{
        final Expression left, right;
        final Token operator;

        public Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinary(this);
        }
    }

    // < expression >           -> < logic_operation >
    // < logic_operation >      -> < expression > < logic_operators >  < expression >
    // < logic_operators >      -> AND | OR | NOT
    static class Logic extends Expression{
        final Expression left, right;
        final Token logic_op;

        public Logic(Expression left, Token logic_op, Expression right) {
            this.left = left;
            this.right = right;
            this.logic_op = logic_op;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogic(this);
        }
    }

    // < expression >   -> < group >
    // < group >        -> OPEN_P < expression > CLOSE_P
    static class Group extends Expression{
        final Expression expression;

        public Group(Expression expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroup(this);
        }
    }

    // < expression >   -> < literal >
    // < literal >        -> INTEGER | DOUBLE | STRING | CHARACTERS | BOOLEAN
    static class Literal extends Expression{
        final Token literal;

        public Literal(Token literal) {
            this.literal = literal;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    // < expression >   -> < assign >
    // < assign >    -> IDENTIFIER ASSIGN_OP ( < assign > | < expression >)
    static class Assign extends Expression{
        final Token name;
        final Expression expression;    // or value

        public Assign(Token name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssign(this);
        }
    }

    // < expression > -> IDENTIFIER
    static class Variable extends Expression{
        final Token name;
        final Expression initializer;

        public Variable(Token name, Expression initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }
    }

}
