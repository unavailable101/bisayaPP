package interpreter;

import lexer.TokenType;
import parser.Expression;
import parser.Statement;

import static lexer.TokenType.*;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    @Override
    public Object visitUnary(Expression.Unary expression) {
        Object right = expression.right;

        switch (expression.op.getType()){
            case ARITH_MINUS:
                return -(Double)right;
            case LOG_NOT:
                return null;
        }

        return null;
    }

    @Override
    public Object visitBinary(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.getType()){
            case ARITH_ADD :
                return (Double)left + (Double)right;
            case ARITH_MINUS :
                return (Double)left - (Double)right;
            case ARITH_MULT :
                return (Double)left * (Double)right;
            case ARITH_DIV :
                return (Double)left / (Double)right;
            case ARITH_MOD :
                return (Double)left % (Double)right;

        }
        return null;
    }

    @Override
    public Object visitLogic(Expression.Logic expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.logic_op.getType()){
            case LOG_AND :
//                return left && right;
                return null;
            case LOG_OR :
//                return left || right;
                return null;
        }

        return null;
    }

    @Override
    public Object visitGroup(Expression.Group expression) {
        return null;
    }

    @Override
    public Object visitLiteral(Expression.Literal expression) {
        return null;
    }

    @Override
    public Object visitAssign(Expression.Assign expression) {
        return null;
    }

    @Override
    public Object visitVariable(Expression.Variable expression) {
        return null;
    }

    @Override
    public Object visitCompare(Expression.Compare expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.op.getType()){
            case ARITH_EQUAL :
                return left.equals(right);
            case ARITH_NOT_EQUAL :
                return !left.equals(right); // ambot
            case ARITH_GT :
                return (Double)left > (Double)right;
            case ARITH_LT :
                return (Double)left < (Double)right;
            case ARITH_LOE :
                return (Double)left <= (Double)right;
            case ARITH_GOE :
                return (Double)left >= (Double)right;
        }

        return null;
    }

    @Override
    public <R> R visitExpr(Statement.Expr statement) {
        return null;
    }

    @Override
    public <R> R visitOutput(Statement.Output statement) {
        return null;
    }

    @Override
    public <R> R visitInput(Statement.Input statement) {
        return null;
    }

    @Override
    public <R> R visitVarDeclaration(Statement.VarDeclaration statement) {
        return null;
    }

    private Object evaluate (Expression expr){
        return expr.accept(this);
    }

}
