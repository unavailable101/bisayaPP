package parser;

import lexer.Token;
import lexer.TokenType;

public abstract class Expression {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R>{
        R visitUnary(Unary expression);
        R visitBinary(Binary expression);
        R visitLogic(Logic expression);
        R visitGroup(Group expression);
        R visitLiteral(Literal expression);
        R visitAssign(Assign expression);
        R visitVariable(Variable expression);
        R visitCompare(Compare expression);
        R visitEscapeCode(EscapeCode code);
        R visitIncrementExpression(IncrementExpression expression);
    }
    // < expression >   -> < unaru >
    // < unary >        -> ( ADD | MINUS) ( < literal > | IDENTIFIER )
    public static class Unary extends Expression{
        public final Token op;
        public final Expression right;

        public Unary(Token op, Expression right) {
            this.op = op;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary(this);
        }
    }

    // < expression >           -> < binary_operation >
    // < binary_operation >     -> < expression > < operator >  < expression >
    // < operator >             -> ADD | MINUS | MULT | DIV | MOD | GT | LT | GOE | LOE | EQUAL | NOT_EQUAL
    public static class Binary extends Expression{
        public final Expression left, right;
        public final Token operator;

        public Binary(Expression left, Token operator, Expression right) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinary(this);
        }
    }

    // < expression >           -> < logic_operation >
    // < logic_operation >      -> < expression > < logic_operators >  < expression >
    // < logic_operators >      -> AND | OR | NOT
    public static class Logic extends Expression{
        public final Expression left, right;
        public final Token logic_op;

        public Logic(Expression left, Token logic_op, Expression right) {
            this.left = left;
            this.right = right;
            this.logic_op = logic_op;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogic(this);
        }
    }

    // < expression >   -> < group >
    // < group >        -> OPEN_P < expression > CLOSE_P
    public static class Group extends Expression{
        public final Expression expression;

        public Group(Expression expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroup(this);
        }
    }

    // < expression >   -> < literal >
    // < literal >        -> INTEGER | DOUBLE | STRING | CHARACTERS | BOOLEAN
    public static class Literal extends Expression{
        public final Object literal;
        public final TokenType type;

        public Literal(Object literal, TokenType type) {
            this.literal = literal;
            this.type = type;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteral(this);
        }

        @Override
        public String toString() {
            return "Lit(" + literal + ")";
        }
    }

    // < expression >   -> < assign >
    // < assign >    -> IDENTIFIER ASSIGN_OP ( < assign > | < expression >)
    public static class Assign extends Expression{
        public final Token name;
        public final Expression expression;    // or value

        public Assign(Token name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssign(this);
        }
    }

    // < expression > -> IDENTIFIER
    public static class Variable extends Expression{
        public final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }

        @Override
        public String toString() {
            return "Var(" + name.getValue() + ")";
        }
    }

    public static class Compare extends Expression{
        public final Expression left, right;
        public final Token op;

        public Compare(Expression left, Token op, Expression right) {
            this.left = left;
            this.right = right;
            this.op = op;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCompare(this);
        }

        @Override
        public String toString() {
            return "(" + op.getValue() + " " + left + " " + right + ")";
        }
    }

    public static class EscapeCode extends Expression {
        public final Token code;

        public EscapeCode(Token code) {
            this.code = code;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitEscapeCode(this);
        }
    }

    public static class IncrementExpression extends Expression {
        public final Token variable;
        public final boolean isPreIncrement; // true = ++x, false = x++

        public IncrementExpression(Token variable, boolean isPreIncrement) {
            this.variable = variable;
            this.isPreIncrement = isPreIncrement;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIncrementExpression(this);
        }

    }
}
