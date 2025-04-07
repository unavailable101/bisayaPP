package interpreter;

import parser.Expression;
import parser.Statement;

import java.util.List;
import java.util.Scanner;

import static errors.Sayop.*;


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
                type = ifNumOperand(right, expression.op.getLine());
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

        double l, r;

        switch (expression.operator.getType()){
            case ARITH_ADD :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left + (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l + r;
                }
            case ARITH_MINUS :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left - (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l - r;
                }
            case ARITH_MULT :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left * (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l * r;
                }
            case ARITH_DIV :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left / (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l / r;
                }
            case ARITH_MOD :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left % (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l % r;
                }
            case CONCAT:
                return stringify(left) + stringify(right);
        }
        return null;
    }

    @Override
    public Object visitLogic(Expression.Logic expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);

        if (!matchDT("TINUOD", left) || !matchDT("TINUOD", right) )
            throw new TypeError(expression.logic_op.getLine(), "\n\tAng value sa first type " + left.toString() + "\n\tAng value sa second type " + right.toString());

        switch (expression.logic_op.getType()){
            case LOG_AND :
                return (boolean)left && (boolean)right;
            case LOG_OR :
                return (boolean)left || (boolean)right;
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

        if (!matchDT(dataType, value)) throw new TypeError(expression.name.getLine(),"Data Type mismatch");

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
        double l,r;
        switch (expression.op.getType()){
            case ARITH_EQUAL :
                return isEqual(left, right);
            case ARITH_NOT_EQUAL :
                return !isEqual(left, right);
            case ARITH_GT :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left > (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l > r;
                }
            case ARITH_LT :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left < (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l < r;
                }
            case ARITH_LOE :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left <= (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l <= r;
                }
            case ARITH_GOE :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left >= (int) right;
                    case 'd' :
                        l = left instanceof Integer ? (int)left : (double)left;
                        r = right instanceof Integer ? (int)right : (double)right;
                        return l >= r;
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
    public Object visitEscapeCode(Expression.EscapeCode code) {
        return code.code.getValue().toString();
    }

    @Override
    public <R> R visitOutput(Statement.Output statement) {
        Object value = evaluate(statement.expression);
        System.out.print(stringify(value));
        return null;
    }

    @Override
    public <R> R visitInput(Statement.Input statement) {
        Scanner sc = new Scanner(System.in);

        String input = sc.nextLine();

        Object value = inputType(input, env.getType(statement.variable.getValue().toString()).getValue().toString(), statement.variable.getLine());
        env.assign(statement.variable, value);

        return null;
    }

    @Override
    public <R> R visitVarDeclaration(Statement.VarDeclaration statement) {
        Object value = null;
        if (statement.initialization != null) {
            value = evaluate(statement.initialization);

            if (!matchDT(statement.type.getValue().toString(), value)) throw new TypeError(statement.type.getLine(), "Cannot assign " + typeof(value) + " to variable of type " + statement.type.getValue().toString());
        }
        env.define(statement.type, statement.var.getValue().toString(), value);
        return null;
    }

    @Override
    public <R> R visitIfStatement(Statement.IfStatement statement) {
        return null;
    }

    @Override
    public <R> R visitBlockStatement(Statement.BlockStatement statement) {
        return null;
    }

    private boolean isTrue(Object o){
        return o == null ? false :
                o instanceof Boolean ? (boolean)o : false;
    }

    private boolean isEqual(Object a, Object b){
        return a == null && b == null ? true :
                a == null || b == null ? false : a.equals(b);
    }

    private char ifNumOperand (Object o, int line){
        if (o instanceof Integer) return 'i';
        if (o instanceof Double) return 'd';
        throw new RuntimeError(line, "Ang "+ o.toString() + " kay " + typeof(o) + ",dili number");
    }

    // ambot if kailangan bha same datatype ang left and right expressions
    // pwede rani ma change
    // rararararararar
    private char ifNumOperands (Object left, Object right, int line){
        if ( left instanceof Integer  && right instanceof Integer) return 'i';
        if (
                ( left instanceof Double || left instanceof Integer ) && right instanceof Double  ||
                left instanceof Double && ( right instanceof Integer || right instanceof Double )
        )  return 'd';
        throw new RuntimeError(line, "\n\tAng first value " + left.toString() + " kay type " + typeof(left) + "\n\tAng second value " + right.toString() + " second type " + typeof(right));
    }

    String stringify (Object value){
        if (value ==  null) return "nil";

        if (value instanceof Integer || value instanceof Double) return value.toString();

        if (value instanceof Boolean) return (Boolean)value ? "OO" : "DILI";

        if (value instanceof String){
            if (value.equals("$")) return "\n";

            String str = (String)value;

            return str.startsWith("\"") && str.endsWith("\"") ? str.substring(1, str.length() - 1) :  str;
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
                return value instanceof Boolean;
            case "LETRA":
                return value instanceof Character;
            default:
                return false;
        }
    }

    private Object inputType (String input, String type, int line){
         switch (type){
             case "NUMERO":
                 return Integer.parseInt(input);
             case "TIPIK":
                 return Double.parseDouble(input);
             case "PISI":
                 return "\"" + input + "\"";
             case "LETRA":
                 if (input.length()!=1) throw new TypeError(line, "usa ra ka character");
                 return Character.valueOf(input.charAt(0));
             default:
                 throw new TypeError(line, "Wa ko kaila");
         }
    }

    private String typeof(Object value){
        return value instanceof Integer ? "NUMERO" :
                value instanceof Double ? "TIPIK" :
                value instanceof Boolean ? "TINUOD" :
                value instanceof String ? "PISI" :
                value instanceof Character ? "LETRA" : "wala";
    }

}
