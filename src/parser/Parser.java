package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static lexer.TokenType.*;

// hello tree, my worst nightmare
// we meet again
// akala ko graduate na ako sayo, pero hindi pa pala


public class Parser {
    private final List<List<Token>> lineTokens;
    private int line;
    int indx = 0;

    private final List<Statement> statements;

    public Parser(List<List<Token>> lineTokens) {
        this.lineTokens = lineTokens;
        this.line = -1;
        this.statements = new ArrayList<>();
    }

    public List<Statement> parse(){

        int size = 0;

        if (lineTokens.getFirst().getFirst() != lineTokens.getFirst().getLast() &&
                lineTokens.getFirst().getFirst().getType() != START_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'SUGOD' before line " + lineTokens.getFirst().getFirst().getLine());
        else ++line;


        if (lineTokens.getLast().getFirst() != lineTokens.getLast().getLast() &&
                lineTokens.getLast().getLast().getType() != END_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'KATAPUSAN' after line " + lineTokens.getLast().getLast().getLine());
        else size = lineTokens.size()-1;

        List<Token> currLine;
        int cl = 0;
        for (int i = 1; i < size; i++) {
            currLine = lineTokens.get(i);
            statement(currLine);
            indx = 0;   //reset to 0 if mana nag read ang usa ka line
            System.out.println("Successful parsed line " + currLine.getFirst().getLine());

            while (cl < statements.size()){
                System.out.println(new ASTPrinter().printStatement(statements.get(cl)));
                cl++;
            }
            System.out.println();

        }

        return statements;
    }

    private void statement(List<Token> tokens){
        switch (currToken(tokens).getType()){
            case VAR_DECLARATION:
                nextToken(tokens);
                statements.addAll(varDeclare(tokens));
                break;
            case OUTPUT:
                nextToken(tokens);
                statements.add(outputStatement(tokens));
                break;
            case INPUT:
                nextToken(tokens);
                statements.addAll(inputStatement(tokens));
                break;
            default:
                statements.add(exprStatement(tokens));
        }
    }

    private List<Statement> varDeclare(List<Token> tokens){
        Token dataType = null;
        if (consume(currToken(tokens), DATA_TYPE, "Walay Data Type at line " + currToken(tokens).getLine())) dataType = currToken(tokens);
        nextToken(tokens);

        List<Statement> declaration = new ArrayList<>();
        boolean expectNext = false;

        consume(currToken(tokens), IDENTIFIER, "Walay variable name at line " + currToken(tokens).getLine());

        while (currToken(tokens).getType() == IDENTIFIER) {
            Expression initialzer = expression(tokens);
            declaration.add(new Statement.VarDeclaration(dataType, initialzer));
            if (currToken(tokens).getType() == COMMA) {
                nextToken(tokens);
                expectNext = true;
            } else {
                expectNext = false;
                break;
            }
        }

        if (expectNext) consume(currToken(tokens), IDENTIFIER, "Walay variable name human sa , at line " + currToken(tokens).getLine());

        return declaration;
    }

    private Statement outputStatement(List<Token> tokens){
            consume(currToken(tokens), COLON, "Walay ':' human sa " + prevToken(tokens) + " at line " + currToken(tokens).getLine());
            nextToken(tokens);
            Expression expr = expression(tokens);
            return new Statement.Output(expr);

    }

    private List<Statement> inputStatement(List<Token> tokens){
            consume(currToken(tokens), COLON, "Walay ':' human sa " + prevToken(tokens) + " at line " + currToken(tokens).getLine());

            nextToken(tokens);
            List<Statement> inputs = new ArrayList<>();

//                Expression expr = expression(tokens);
            while (true) {
                consume(currToken(tokens), IDENTIFIER, "Walay variable para input at line " + currToken(tokens).getLine());
                inputs.add(new Statement.Input(currToken(tokens)));
                nextToken(tokens);
                if (currToken(tokens).getType() == COMMA) nextToken(tokens);
                else break;
            }

            return inputs;
    }

    // < expr_statement >   -> < expression >
    private Statement exprStatement (List<Token> tokens){
        /*
        modify this where it will handle statements like
            x
        it should not be accepted kay variable ra ang gi butang
        */
        Expression expr = expression(tokens);
        return new Statement.Expr(expr);
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

        if ( currToken(tokens).getType() == ASS_OP) {
            if (expr instanceof Expression.Variable) {
                Token var = prevToken(tokens);
                nextToken(tokens);
                return new Expression.Assign(var, assignment(tokens));
            } else throw new IllegalArgumentException("Walay variable before '=' sa line " + currToken(tokens).getLine());
        }

        return expr;
    }

    private Expression logicalOr(List<Token> tokens){

        Expression expr = logicalAnd(tokens);

        while (
                currToken(tokens).getType() == LOG_OR
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = logicalAnd(tokens);
//            nextToken(tokens);
            expr = new Expression.Binary(expr, op, right);
        }

        return expr;
    }

    private Expression logicalAnd(List<Token> tokens){

        Expression expr = equality(tokens);

        while (
                currToken(tokens).getType() == LOG_AND
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = equality(tokens);
//            nextToken(tokens);
            expr = new Expression.Logic(expr, op, right);
        }

        return expr;
    }

    private Expression equality(List<Token> tokens){

        Expression expr = comparison(tokens);

        while (
                currToken(tokens).getType() == ARITH_EQUAL ||
                currToken(tokens).getType() == ARITH_NOT_EQUAL
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = comparison(tokens);
//            nextToken(tokens);
            expr = new Expression.Compare(expr, op, right);
        }

        return expr;
    }

    private Expression comparison (List<Token> tokens){

        Expression expr = term(tokens);

        while (
                currToken(tokens).getType() == ARITH_GT ||
                currToken(tokens).getType() == ARITH_LT ||
                currToken(tokens).getType() == ARITH_GOE ||
                currToken(tokens).getType() == ARITH_LOE
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = term(tokens);
//            nextToken(tokens);
            expr = new Expression.Compare(expr, op, right);
        }

        return expr;
    }

    private Expression term (List<Token> tokens){

        Expression expr = factor(tokens);

        while (
                currToken(tokens).getType() == ARITH_ADD    ||
                currToken(tokens).getType() == ARITH_MINUS  ||
                currToken(tokens).getType() == CONCAT       // para string
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = factor(tokens);
//            nextToken(tokens);
            expr = new Expression.Binary(expr, op, right);
        }

        return expr;
    }

    private Expression factor(List<Token> tokens){

        Expression expr = unary(tokens);

        while (
                currToken(tokens).getType() == ARITH_MULT ||
                currToken(tokens).getType() == ARITH_DIV ||
                currToken(tokens).getType() == ARITH_MOD
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = unary(tokens);
//            nextToken(tokens);
            expr = new Expression.Binary(expr, op, right);
        }

        return expr;
    }

    private Expression unary(List<Token> tokens){

        if (
                currToken(tokens).getType() == ARITH_ADD ||
                currToken(tokens).getType() == ARITH_MINUS ||
                currToken(tokens).getType() == LOG_AND
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
//            System.out.println(currToken(tokens));
            Expression right = unary(tokens);
            return new Expression.Unary(op, right);
        }

        return primary(tokens);
    }

    private Expression primary(List<Token> tokens){

        Expression expr;

        switch (currToken(tokens).getType()){
            //variables
            case IDENTIFIER:
                expr = new Expression.Variable(currToken(tokens));
                nextToken(tokens);
                return expr;

            //literals
            case INTEGER:
            case DOUBLE:
            case BOOLEAN:
            case CHARACTERS:
            case STRING:
            case NEW_LINE:
                expr = new Expression.Literal(currToken(tokens).getValue(), currToken(tokens).getType());
                nextToken(tokens);
                return expr;

             //if group sha
            case ARITH_OPEN_P:
                nextToken(tokens);
                expr = expression(tokens);
                if (currToken(tokens).getType() != ARITH_CLOSE_P) throw new RuntimeException("Expected ')' after expression.");
                nextToken(tokens);
                return new Expression.Group(expr);

        }

        throw new RuntimeException("Expected expression.");
    }

    private Token nextToken (List<Token> tokens){
        return indx < tokens.size() ? tokens.get(indx++) : tokens.get(tokens.size()-1);
    }

    private Token currToken (List<Token> tokens){
        return indx < tokens.size() ? tokens.get(indx) : tokens.get(tokens.size()-1);
    }

    private Token prevToken (List<Token> tokens){
        return tokens.get(indx-1);
    }

    private boolean consume(Token curr, TokenType type, String error){
        if (curr == null) throw new IllegalArgumentException(error);
        if (curr.getType() == type) return true;
        throw new IllegalArgumentException(error);
    }

}
