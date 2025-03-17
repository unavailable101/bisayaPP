package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

// hello tree, my worst nightmare
// we meet again
// akala ko graduate na ako sayo, pero hindi pa pala

public class Parser {
    private final List<List<Token>> lineTokens;
    private int line;
    int indx = 0;

    public Parser(List<List<Token>> lineTokens) {
        this.lineTokens = lineTokens;
        this.line = -1;
    }

    public List<Statement> parse(){

        int size = 0;

        if (lineTokens.getFirst().getFirst() != lineTokens.getFirst().getLast() &&
                lineTokens.getFirst().getFirst().getType() != TokenType.START_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'SUGOD' before line " + lineTokens.getFirst().getFirst().getLine());
        else ++line;


        if (lineTokens.getLast().getFirst() != lineTokens.getLast().getLast() &&
                lineTokens.getLast().getLast().getType() != TokenType.END_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'KATAPUSAN' after line " + lineTokens.getLast().getLast().getLine());
        else size = lineTokens.size()-1;

        //dire rako kutob eh
        List<Statement> statements = new ArrayList<>();

        for (int i = 1; i < size; i++) {
            statements.add(statement(lineTokens.get(i)));
            indx = 0;   //reset to 0 if mana nag read ang usa ka line
        }

        return statements;
    }

    private Statement statement(List<Token> tokens){
        switch (currToken(tokens).getType()){
            case TokenType.OUTPUT:
                nextToken(tokens);
                return outputStatement(tokens);
            case TokenType.INPUT:
                nextToken(tokens);
                return inputStatement(tokens);
            case TokenType.VAR_DECLARATION:
                nextToken(tokens);
                return varDeclare(tokens);
            default:
                return exprStatement(tokens);
        }
    }

    private Statement varDeclare(List<Token> tokens){
        Token dataType = consume(currToken(tokens), TokenType.DATA_TYPE, "Walay Data Type");
        Expression initialzer = expression(tokens);
        return new Statement.VarDeclaration(dataType, initialzer);
    }

    private Statement outputStatement(List<Token> tokens){
        if (currToken(tokens).getType() == TokenType.COLON)  return new Statement.Output(expression(tokens));       // sure sd ka na expression ang naa dire? omg nimo giiirl
        throw new RuntimeException("Missing ':' after " + tokens.getFirst());
    }

    private Statement inputStatement(List<Token> tokens){
        if (currToken(tokens).getType() == TokenType.COLON)  return new Statement.Input(expression(tokens));        //sure ka na expression ni dire girl?
        throw new RuntimeException("Missing ':' after " + tokens.getFirst());
    }

    // < expr_statement >   -> < expression >
    private Statement exprStatement (List<Token> tokens){
        return new Statement.Expr(expression(tokens));
    }

    // < expression >       -> < literal >             |
    //                         IDENTIFIER              |
    //                         < unary_expression >    |
    //                         < binary_operation >    |
    //                         < logic_operation >     |
    //                         < group_expression >    |
    //                         < assign >
    private Expression expression(List<Token> tokens){
        return assignment(tokens);
    }

    private Expression assignment(List<Token> tokens){
        Expression expr = logicalOr(tokens);

        if (
                currToken(tokens).getType() == TokenType.IDENTIFIER &&
                nextToken(tokens).getType() == TokenType.ASS_OP
        ){
            Token var = prevToken(tokens);
            nextToken(tokens);
            return new Expression.Assign(var, assignment(tokens));
        }

        return expr;
    }

    private Expression logicalOr(List<Token> tokens){
        Expression expr = logicalAnd(tokens);

        while (
                currToken(tokens).getType() == TokenType.LOG_OR
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = logicalAnd(tokens);
            nextToken(tokens);
            expr = new Expression.Binary(expr, op, right);
        }

        return expr;
    }

    private Expression logicalAnd(List<Token> tokens){
        Expression expr = equality(tokens);

        while (
                currToken(tokens).getType() == TokenType.LOG_AND
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = equality(tokens);
            nextToken(tokens);
            expr = new Expression.Logic(expr, op, right);
        }

        return expr;
    }

    private Expression equality(List<Token> tokens){
        Expression expr = comparison(tokens);

        while (
                currToken(tokens).getType() == TokenType.ARITH_EQUAL ||
                currToken(tokens).getType() == TokenType.ARITH_NOT_EQUAL
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = comparison(tokens);
            nextToken(tokens);
            expr = new Expression.Compare(expr, op, right);
        }

        return expr;
    }

    private Expression comparison (List<Token> tokens){
        Expression expr = term(tokens);

        while (
                currToken(tokens).getType() == TokenType.ARITH_GT ||
                currToken(tokens).getType() == TokenType.ARITH_LT ||
                currToken(tokens).getType() == TokenType.ARITH_GOE ||
                currToken(tokens).getType() == TokenType.ARITH_LOE
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = term(tokens);
            nextToken(tokens);
            expr = new Expression.Compare(expr, op, right);
        }

        return expr;
    }

    private Expression term (List<Token> tokens){
        Expression expr = factor(tokens);

        while (
                currToken(tokens).getType() == TokenType.ARITH_ADD ||
                currToken(tokens).getType() == TokenType.ARITH_MINUS
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = factor(tokens);
            nextToken(tokens);
            expr = new Expression.Binary(expr, op, right);
        }

        return expr;
    }

    private Expression factor(List<Token> tokens){
        Expression expr = unary(tokens);

        while (
                currToken(tokens).getType() == TokenType.ARITH_MULT ||
                currToken(tokens).getType() == TokenType.ARITH_DIV ||
                currToken(tokens).getType() == TokenType.ARITH_MOD
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = unary(tokens);
            nextToken(tokens);
            expr = new Expression.Binary(expr, op, right);
        }

        return expr;
    }

    private Expression unary(List<Token> tokens){
        if (
                currToken(tokens).getType() == TokenType.ARITH_ADD ||
                currToken(tokens).getType() == TokenType.ARITH_MINUS ||
                currToken(tokens).getType() == TokenType.LOG_AND
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = unary(tokens);
            return new Expression.Unary(op, right);
        }

        return primary(tokens);
    }

    private Expression primary(List<Token> tokens){

        Expression expr;

        switch (currToken(tokens).getType()){
            //literals
            case TokenType.INTEGER:
            case TokenType.DOUBLE:
            case TokenType.BOOLEAN:
            case TokenType.CHARACTERS:
            case TokenType.STRING:
                expr = new Expression.Literal(currToken(tokens));
                nextToken(tokens);
                return expr;

            //variables
            case TokenType.IDENTIFIER:
                expr = new Expression.Variable(currToken(tokens));
                nextToken(tokens);
                return expr;

             //if group sha
            case TokenType.ARITH_OPEN_P:
                nextToken(tokens);
                expr = expression(tokens);
                if (currToken(tokens).getType() != TokenType.ARITH_CLOSE_P) throw new RuntimeException("Expected ')' after expression.");
                nextToken(tokens);
                return new Expression.Group(expr);
        }

        throw new RuntimeException("Expected expression.");
    }

    private Token nextToken (List<Token> tokens){
        return indx < tokens.size() ? tokens.get(indx++) : tokens.get(tokens.size()-1);
    }

    private Token currToken (List<Token> tokens){
        return indx  > tokens.size() ? tokens.get(indx) : tokens.get(tokens.size()-1);
    }

    private Token prevToken (List<Token> tokens){
        return tokens.get(indx-1);
    }

    private Token consume(Token curr, TokenType type, String error){
        if (curr == null) throw new IllegalArgumentException(error);
        if (curr.getType() == type) return curr;
        throw new IllegalArgumentException(error);
    }

    private boolean check (Token curr, TokenType type){
        if (curr == null) throw new IllegalArgumentException();
        return curr.getType() == type;
    }

}
