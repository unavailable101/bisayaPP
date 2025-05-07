package interpreter;

import parser.Expression;
import parser.Statement;

import java.util.List;
import java.util.Scanner;

import static errors.Sayop.*;


public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {

    private Environment env = new Environment();

    public void execute(Statement statement){
        statement.accept(this);
    }

    private Object evaluate (Expression expr){
        return  expr.accept(this);
    }



    public void interpret (List<Statement> statements){
        for (Statement statement : statements){
            execute(statement);
        }
    }

    @Override
    public Object visitUnary(Expression.Unary expression){
        Object right = evaluate((expression.right));
        char type;
        switch (expression.op.getType()){
            case ARITH_MINUS:
                type = ifNumOperand(right, expression.op.getLine());
                switch (type){
                    case 'i':
                        return  -(int) right;
                    case 'd':
                        return  -(int) right;
                }
            case LOG_NOT:
                return  !isTrue(right);
        }
        return  null;
    }

    @Override
    public Object visitBinary(Expression.Binary expression) {
         Object left = evaluate(expression.left);
         Object right = evaluate(expression.right);
//        Object left = evaluate(expression.left);
//        Object right = evaluate(expression.right);

         double ls, rs;

        switch (expression.operator.getType()){
            case ARITH_ADD :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left + (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls + rs;
                }
            case ARITH_MINUS :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left - (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls - rs;
                }
            case ARITH_MULT :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left * (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls * rs;
                }
            case ARITH_DIV :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left / (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls / rs;
                }
            case ARITH_MOD :
                switch (ifNumOperands(left, right, expression.operator.getLine())) {
                    case 'i' : return (int) left % (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls % rs;
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
            case LOG_OR :
                return (boolean)left || (boolean)right;
            case LOG_AND :
                return (boolean)left && (boolean)right;

        }

        return null;
    }

    @Override
    public Object visitGroup(Expression.Group expression) {
        return evaluate(expression.expression);
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
    public Object visitLiteral(Expression.Literal expression) {
        return expression.literal;
    }



    @Override
    public Object visitCompare(Expression.Compare expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        double ls,rs;
        switch (expression.op.getType()){
            case ARITH_EQUAL :
                return isEqual(left, right);
            case ARITH_NOT_EQUAL :
                return !isEqual(left, right);
            case ARITH_GT :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left > (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls > rs;
                }
            case ARITH_LT :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left < (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls < rs;
                }

            case ARITH_GOE :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left >= (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls >= rs;
                }

            case ARITH_LOE :
                switch (ifNumOperands(left, right, expression.op.getLine())) {
                    case 'i' : return (int) left <= (int) right;
                    case 'd' :
                        if(left instanceof Integer){
                            ls = (int)left;
                        }else{
                            ls = (double)left;
                        }
                        if(right instanceof Integer){
                            rs = (int)right;
                        }else{
                            rs = (double)right;
                        }
                        return ls <= rs;
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
        System.out.print(stringify(value));
        return null;
    }

    @Override
    public Object visitEscapeCode(Expression.EscapeCode code) {
        return code.code.getValue().toString();
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
    public <R> R visitInput(Statement.Input statement) {
        Scanner sc = new Scanner(System.in);

        String input = sc.nextLine();

        Object value = inputType(input, env.getType(statement.variable.getValue().toString()).getValue().toString(), statement.variable.getLine());
        env.assign(statement.variable, value);

        return null;
    }



    @Override
    public <R> R visitIncrementStatement(Statement.IncrementStatement statement) {
        var variable = statement.variableName;

        Object value = env.get(variable);

        if (!(value instanceof Integer)) {
            throw new RuntimeError(variable.getLine(), "Variable '" + variable.getValue().toString() + "' is not an integer.");
        }

        int incrementedValue = (Integer) value + 1;
        env.assign(variable, incrementedValue);

        return null;
    }

    @Override
    public Object visitIncrementExpression(Expression.IncrementExpression expr) {
        var variable = expr.variable;

        Object value = env.get(variable);

        if (!(value instanceof Integer)) {
            throw new RuntimeError(variable.getLine(), "Variable '" + variable.getValue().toString() + "' is not an integer.");
        }

        int intValue = (Integer) value;

        if (expr.isPreIncrement) {
            intValue++;
            env.assign(expr.variable, intValue); // update the value
            return intValue;
        } else {
            int original = intValue;
            intValue++;
            env.assign(expr.variable, intValue); // update the value
            return original;
        }
    }


    @Override
    public <R> R visitIfStatement(Statement.IfStatement statement) {
        // Evaluate the condition of the 'if' statement
//        System.out.println("statement.condition: " + statement.condition);
//        System.out.println("Condition Result: " + conditionResult);
        Object conditionResult = evaluate(statement.condition);

        // If the condition evaluates to true, execute the 'then' block
        if (isTrue(conditionResult)) {
            execute(statement.thenBlock);
        }
        // Check if there are multiple 'else if' blocks and evaluate each one in sequence
        else {
            for (int i = 0; i < statement.elseIfConditions.size(); i++) {
                // Evaluate each 'else if' condition
                Object elseIfCondition = evaluate(statement.elseIfConditions.get(i));
                if (isTrue(elseIfCondition)) {
                    // If a condition matches, execute the corresponding 'else if' block
                    execute(statement.elseIfBlocks.get(i));
                    return null; // Exit after executing the first matching 'else if'
                }
            }

            // If no 'else if' blocks match, check if there is an 'else' block and execute it
            if (statement.elseBlock != null) {
                execute(statement.elseBlock);
            }
        }

        return null;
    }

    @Override
    public <R> R visitForStatement(Statement.ForStatement statement) {
        if (statement.initializer != null) {
            execute(statement.initializer);
        }

        while (isTrue(evaluate(statement.condition))) {
            try {
                execute(statement.block);
            } catch (Breakout b) {
                break;
            } catch (ContinueNext c) {

            }

            if (statement.increment != null) {
                evaluate(statement.increment);
            }
        }

        return null;
    }


    @Override
    public <R> R visitBlockStatement(Statement.BlockStatement statement) {
        Environment previous = this.env;
        try {
            this.env = new Environment(previous);
            for (Statement stmt : statement.statements) execute(stmt);
        }
        finally {
            this.env = previous;
        }
        return null;
    }

    @Override
    public <R> R visitWhileStatement(Statement.WhileStatement statement) {
        while (isTrue(evaluate(statement.condition))) execute(statement.thenBlock);
        return null;
    }

    @Override
    public <R> R visitBreakStatement(Statement.BreakStatement statement) {
        throw new Breakout(statement.keyword);
    }

    @Override
    public <R> R visitContinueStatement(Statement.ContinueStatement statement) {
        throw new ContinueNext(statement.keyword);
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


    private String typeof(Object value){
        return value instanceof Integer ? "NUMERO" :
               value instanceof Double ? "TIPIK" :
               value instanceof Boolean ? "TINUOD" :
               value instanceof String ? "PISI" :
               value instanceof Character ? "LETRA" : "wala";
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
            case "TINUOD":
                if (
                        input.equals("\"OO\"") ||
                                input.equals("\"DILI\"")
                ) return input.equals("\"OO\"") ? Boolean.TRUE : Boolean.FALSE;
            case "LETRA":
                if (input.length()!=1) throw new TypeError(line, "usa ra ka character");
                return Character.valueOf(input.charAt(0));
            default:
                throw new TypeError(line, "Wa ko kaila");
        }
    }



}
