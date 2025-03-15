package parser;

import lexer.Token;

public abstract class Expression {
    static class Unary extends Expression{
        final Token op;

        public Unary(Token op) {
            this.op = op;
        }
    }

    static class Binary extends Expression{
        final Expression left, right;
        final Token operator;

        public Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }
    }

    static class Group extends Expression{
        final Expression expression;

        public Group(Expression expression) {
            this.expression = expression;
        }
    }

    static class Literal extends Expression{
        final Token literalType;

        public Literal(Token literalType) {
            this.literalType = literalType;
        }
    }

}
