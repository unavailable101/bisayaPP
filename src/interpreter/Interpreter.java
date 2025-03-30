package interpreter;

import lexer.TokenType;
import parser.Expression;
import parser.Statement;

import java.util.List;


public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {

    private Environment env = new Environment();

    public void interpret (List<Statement> statements){
        for (Statement statement : statements){
            execute(statement);
        }
    }

    public void execute(Statement statement){
        statement.accept(this);
    }

    private Object evaluate (Expression expr){
        return expr.accept(this);
    }

    @Override
    public Object visitUnary(Expression.Unary expression) {
        Object right = evaluate(expression.right);
        char type;
        switch (expression.op.getType()){
            case ARITH_MINUS:
                type = ifNumOperand(right);
                switch (type) {
                    case 'i': return -(int) right;
                    case 'd': return -(int) right;
                }
            case LOG_NOT:
                return !isTrue(right);
        }

        return null;
    }

    @Override
    public Object visitBinary(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        char type;
        switch (expression.operator.getType()){
            case ARITH_ADD :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left + (int) right;
                    case 'd' : return (int) left + (int) right;
                }
            case ARITH_MINUS :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left - (int) right;
                    case 'd' : return (int) left - (int) right;
                }
                return (int)left - (int)right;
            case ARITH_MULT :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left * (int) right;
                    case 'd' : return (int) left * (int) right;
                }
                return (int)left * (int)right;
            case ARITH_DIV :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left / (int) right;
                    case 'd' : return (int) left / (int) right;
                }
                return (int)left / (int)right;
            case ARITH_MOD :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left % (int) right;
                    case 'd' : return (int) left % (int) right;
                }
                return (int)left % (int)right;
            case CONCAT:
                return stringify(left) + stringify(right);
        }
        return null;
    }

    //ambot
    @Override
    public Object visitLogic(Expression.Logic expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        switch (expression.logic_op.getType()){
            case LOG_AND :
                return isTrue(left) && isTrue(right);
//                return null;
            case LOG_OR :
                return isTrue(left) || isTrue(right);
//                return null;
        }

        return null;
    }

    @Override
    public Object visitGroup(Expression.Group expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitLiteral(Expression.Literal expression) {
        return expression.literal;
    }

    @Override
    public Object visitAssign(Expression.Assign expression) {
        Object value = evaluate(expression.expression);

        String dataType = env.getType(expression.name.getValue().toString()).getValue().toString();

        if (!matchDT(dataType, value)) throw new RuntimeException("Data Type mismatch");

        env.assign(expression.name, value);
        return value;
    }

    @Override
    public Object visitVariable(Expression.Variable expression) {
        return env.get(expression.name);
    }

    @Override
    public Object visitCompare(Expression.Compare expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        char type;
        switch (expression.op.getType()){
            case ARITH_EQUAL :
                return isEqual(left, right);
            case ARITH_NOT_EQUAL :
                return !isEqual(left, right);
            case ARITH_GT :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left > (int) right;
                    case 'd' : return (int) left > (int) right;
                }
            case ARITH_LT :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left < (int) right;
                    case 'd' : return (int) left < (int) right;
                }
            case ARITH_LOE :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left <= (int) right;
                    case 'd' : return (int) left <= (int) right;
                }
            case ARITH_GOE :
                type = ifNumOperands(left, right);
                switch (type) {
                    case 'i' : return (int) left >= (int) right;
                    case 'd' : return (int) left >= (int) right;
                }
        }

        return null;
    }

    @Override
    public <R> R visitExpr(Statement.Expr statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public <R> R visitOutput(Statement.Output statement) {
        Object value = evaluate(statement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public <R> R visitInput(Statement.Input statement) {
        return null;
    }

    @Override
    public <R> R visitVarDeclaration(Statement.VarDeclaration statement) {
        Object value = null;
        if (statement.initialization != null) value = evaluate(statement.initialization);
        env.define(statement.type, statement.var.getValue().toString(), value);
        return null;
    }

    private boolean isTrue(Object o){
        return o == null ? false :
                o instanceof Boolean ? (boolean)o : true;
    }

    private boolean isEqual(Object a, Object b){
        return a == null && b == null ? true :
                a == null || b == null ? false : a.equals(b);
    }

    private char ifNumOperand (Object o){
        if (o instanceof Integer) return 'i';
        if (o instanceof Double) return 'd';
        throw new RuntimeException("Dili ni sha number unary");
    }

    private char ifNumOperands (Object left, Object right){
        if ( left instanceof Integer && right instanceof Integer) return 'i';
        if ( left instanceof Double && right instanceof Double) return 'd';
        throw new RuntimeException("Dili ni sha number binary");
    }

    String stringify (Object value){
        if (value ==  null) return "nil";

        if (value instanceof Integer || value instanceof Double) return value.toString();

        if (value instanceof Boolean || value instanceof String){
            if (value.equals("$")) return "\n";

            String str = (String)value;

            if (
                    str.startsWith("\"") && str.endsWith("\"") ||
                    str.startsWith("'") && str.endsWith("'")
            ) {
                return str.substring(1, str.length() - 1);
            }
            return str;
        }

        return value.toString();
    }

    private boolean matchDT(String type, Object value){
        switch (type){
            case "NUMERO":
                return value instanceof Integer;
            case "TIPIK":
                return value instanceof Double;
            case "PISI":
                return value instanceof String;
            case "TINUOD":
                return value instanceof String ?
                    value.equals("\"OO\"") || value.equals("\"DILI\"") : false;
            case "LETRA":
                return true;
            default:
                return false;
        }
    }

}
