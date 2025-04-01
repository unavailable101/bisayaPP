package parser;

class ASTPrinter implements Statement.Visitor<String>, Expression.Visitor<String>{

    String printStatement(Statement stmt){
        return stmt.accept(this);
    }
    String printExpr(Expression expr){
        return expr.accept(this);
    }

    private String parenthesize (String name, Expression... expr){
        StringBuilder output = new StringBuilder();

        output.append("(").append(name);
        for (Expression e : expr){
            output.append(" ");
            output.append(e.accept(this));
        }
        output.append(")");

        return output.toString();
    }

    private String parenthesizeStmt(String name, String... parts) {
        StringBuilder output = new StringBuilder();

        output.append("(").append(name);
        for (String part : parts) {
            output.append(" ");
            output.append(part);
        }
        output.append(")");

        return output.toString();
    }

    @Override
    public String visitUnary(Expression.Unary expression) {
        return parenthesize(expression.op.getValue().toString(), expression.right);
    }

    @Override
    public String visitBinary(Expression.Binary expression) {
        return parenthesize(expression.operator.getValue().toString(), expression.left, expression.right);
    }

    @Override
    public String visitLogic(Expression.Logic expression) {
        return parenthesize(expression.logic_op.getValue().toString(), expression.left, expression.right);
    }

    @Override
    public String visitGroup(Expression.Group expression) {
        return parenthesize("group", expression.expression);
    }

    @Override
    public String visitLiteral(Expression.Literal expression) {
        if (expression.literal == null) return "nil";
        return expression.literal.toString();
    }

    @Override
    public String visitAssign(Expression.Assign expression) {
        return parenthesize("assign " + expression.name.getValue().toString(), expression.expression);
    }

    @Override
    public String visitVariable(Expression.Variable expression) {
        return expression.name.getValue().toString();
    }

    @Override
    public String visitCompare(Expression.Compare expression) {
        return parenthesize(expression.op.getValue().toString(), expression.left, expression.right);
    }

    @Override
    public String visitExpr(Statement.Expr statement) {
        return parenthesizeStmt("Statement expression: ", printExpr(statement.expression));
    }

    @Override
    public String visitOutput(Statement.Output statement) {
        return parenthesizeStmt("Statement output: ", printExpr(statement.expression));
    }

    @Override
    public String visitInput(Statement.Input statement) {
        // Assuming Input statement has an expression field like Output
        return parenthesizeStmt("Statement input: ", statement.variable.getValue().toString());
    }

    @Override
    public String visitVarDeclaration(Statement.VarDeclaration statement) {
        return parenthesizeStmt(
                "Statement var declare type: " + statement.type.getValue().toString() + " " + statement.var.getValue().toString(),
                statement.initialization!=null ? printExpr(statement.initialization) : "nil"
        );
    }
}
