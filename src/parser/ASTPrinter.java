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
    public String visitEscapeCode(Expression.EscapeCode code) {
        return code.code.getValue().toString();
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

//    @Override
//    public String visitIfStatement(Statement.IfStatement statement) {
//        String conditionStr = statement.condition != null ? printExpr(statement.condition) : "nil";
//        String thenStr = statement.thenBlock != null ? printStatement(statement.thenBlock) : "nil";
//        String elseStr = statement.elseBlock != null ? printStatement(statement.elseBlock) : "nil";
//
//        return parenthesizeStmt(
//                "If Statement:",
//                "\n\t condition: " + conditionStr,
//                "\n\t then: " + thenStr,
//                "\n\t else: " + elseStr
//        );
//    }

    @Override
    public String visitIfStatement(Statement.IfStatement statement) {
        StringBuilder builder = new StringBuilder();

        String conditionStr = statement.condition != null ? printExpr(statement.condition) : "nil";
        String thenStr = statement.thenBlock != null ? printStatement(statement.thenBlock) : "nil";

        builder.append("If Statement:")
                .append("\n\t condition: ").append(conditionStr)
                .append("\n\t then: ").append(thenStr);

        if (statement.elseIfConditions != null && !statement.elseIfConditions.isEmpty()) {
            for (int i = 0; i < statement.elseIfConditions.size(); i++) {
                String elseIfConditionStr = printExpr(statement.elseIfConditions.get(i));
                String elseIfBlockStr = printStatement(statement.elseIfBlocks.get(i));
                builder.append("\n\t else if [KUNG DILI]: ").append(elseIfConditionStr)
                        .append("\n\t\t block: ").append(elseIfBlockStr);
            }
        }

        if (statement.elseBlock != null) {
            String elseStr = printStatement(statement.elseBlock);
            builder.append("\n\t else [KUNG WALA]: ").append(elseStr);
        }

        return builder.toString();
    }

    @Override
    public String visitBlockStatement(Statement.BlockStatement statement) {
        StringBuilder block = new StringBuilder();

        for (Statement stmt : statement.statements) block.append("\n\t\t\t\t").append(printStatement(stmt));

        return parenthesizeStmt("Block Statements", block.toString());
    }

    @Override
    public String visitWhileStatement(Statement.WhileStatement statement) {
        String conditionStr = statement.condition != null ? printExpr(statement.condition) : "nil";
        String thenStr = statement.thenBlock != null ? printStatement(statement.thenBlock) : "nil";

        return parenthesizeStmt(
                "While Statement:",
                "\n\t condition: " + conditionStr,
                "\n\t then: " + thenStr
        );
    }

    @Override
    public String visitForStatement(Statement.ForStatement statement) {
        StringBuilder builder = new StringBuilder();

        String initializerStr = statement.initializer != null
                ? printStatement(statement.initializer)
                : "wala";
        String conditionStr = statement.condition != null
                ? printExpr(statement.condition)
                : "wala";
        String incrementStr = statement.increment != null
                ? printExpr(statement.increment)
                : "wala";
        String blockStr = statement.block != null
                ? printStatement(statement.block)
                : "wala";

        builder.append("For Loop:")
                .append("\n\tinitializer: ").append(initializerStr)
                .append("\n\tcondition: ").append(conditionStr)
                .append("\n\tupdate (increment): ").append(incrementStr)
                .append("\n\tBlock: ").append(blockStr);

        return builder.toString();
    }
    @Override
    public String visitBreakStatement(Statement.BreakStatement statement){
        return parenthesizeStmt("Break Statement: " + statement.keyword);
    }

    @Override
    public String visitContinueStatement(Statement.ContinueStatement statement){
        return parenthesizeStmt("Continue Statement: " + statement.keyword);
    }
}
