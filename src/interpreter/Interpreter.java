package interpreter;

import parser.Expression;

public class Interpreter implements Expression.Visitor<Object> {
    @Override
    public Object visitUnary(Expression.Unary expression) {
        return null;
    }

    @Override
    public Object visitBinary(Expression.Binary expression) {
        return null;
    }

    @Override
    public Object visitLogic(Expression.Logic expression) {
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
        return null;
    }
}
