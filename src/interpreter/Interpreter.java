package interpreter;

import parser.Expression;
import parser.Statement;

import java.util.List;


public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {

    public void interpret (List<Statement> statements){
        // dire sha mag kuha(?) sa mga ano, mga statements then iya i pasa to idk
        // iterate bha daaaaaaa kapoy
        // mag read sha each statements then i pasa to another funtcion then gauuur

    }

    @Override
    public Object visitUnary(Expression.Unary expression) {
        Object right = evaluate(expression.right);

        switch (expression.op.getType()){
            case ARITH_MINUS:
                ifNumOperand(right);
                return -(double)right;
            case LOG_NOT:
                return !isTrue(right);
        }

        return null;
    }

    @Override
    public Object visitBinary(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.operator.getType()){
            case ARITH_ADD :
                ifNumOperands(left, right);
                return (double)left + (double)right;
            case ARITH_MINUS :
                ifNumOperands(left, right);
                return (double)left - (double)right;
            case ARITH_MULT :
                ifNumOperands(left, right);
                return (double)left * (double)right;
            case ARITH_DIV :
                ifNumOperands(left, right);
                return (double)left / (double)right;
            case ARITH_MOD :
                ifNumOperands(left, right);
                return (double)left % (double)right;
            case CONCAT:
                return left + (String)right;

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
        return expression.literal;
    }

    @Override
    public Object visitAssign(Expression.Assign expression) {
        Object expr = evaluate(expression.expression);
        return null;
    }

    @Override
    public Object visitVariable(Expression.Variable expression) {
        return expression.name;
    }

    @Override
    public Object visitCompare(Expression.Compare expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.op.getType()){
            case ARITH_EQUAL :
                return isEqual(left, right);
            case ARITH_NOT_EQUAL :
                return !isEqual(left, right);
            case ARITH_GT :
                return (double)left > (double)right;
            case ARITH_LT :
                return (double)left < (double)right;
            case ARITH_LOE :
                return (double)left <= (double)right;
            case ARITH_GOE :
                return (double)left >= (double)right;
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

    private boolean isTrue(Object o){
        return o == null ? false :
                o instanceof Boolean ? (boolean)o : true;
    }

    private boolean isEqual(Object a, Object b){
        return a == null && b == null ? true :
                a == null || b == null ? false : a.equals(b);
    }

    private void ifNumOperand (Object o){
        if (o instanceof Number) return;
        throw new RuntimeException("Dili ni sha number");
    }

    private void ifNumOperands (Object left, Object right){
        if (left instanceof Number &&
            right instanceof Number) return;
        throw new RuntimeException("Dili ni sha number");
    }

}
