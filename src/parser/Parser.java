package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static lexer.TokenType.*;

// hello tree, my worst nightmare
// we meet again
// akala ko graduate na ako sayo, pero hindi pa pala


/*
    TODO:
        - disregard/skip comments
        - tanaw output
*/

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
                lineTokens.getFirst().getFirst().getType() != START_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'SUGOD' before line " + lineTokens.getFirst().getFirst().getLine());
        else ++line;


        if (lineTokens.getLast().getFirst() != lineTokens.getLast().getLast() &&
                lineTokens.getLast().getLast().getType() != END_PROG)
            //dapat SyntaxError ni dire
            throw new IllegalArgumentException("Expected 'KATAPUSAN' after line " + lineTokens.getLast().getLast().getLine());
        else size = lineTokens.size()-1;

        //dire rako kutob eh
        List<Statement> statements = new ArrayList<>();
        List<Token> currLine;
        for (int i = 1; i < size; i++) {

            currLine = lineTokens.get(i);

            if (currToken(currLine).getType() == VAR_DECLARATION) {
                nextToken(currLine);
                List<Statement> declaration;
                declaration = varDeclare(currLine);
                statements.addAll(declaration);
            } else statements.add(statement(currLine));

            indx = 0;   //reset to 0 if mana nag read ang usa ka line
            System.out.println("Successful parsed line " + currLine.getFirst().getLine());

//            System.out.println(new ASTPrinter().printStatement(statements.get(i-1)));
//            System.out.println();
        }
        System.out.println();
        for (Statement s : statements) System.out.println(new ASTPrinter().printStatement(s) + '\n');

        return statements;
    }

    private Statement statement(List<Token> tokens){
        switch (currToken(tokens).getType()){
            case OUTPUT:
                nextToken(tokens);
                return outputStatement(tokens);
            case INPUT:
                nextToken(tokens);
                return inputStatement(tokens);
            default:
                return exprStatement(tokens);
        }
    }

    private List<Statement> varDeclare(List<Token> tokens){
        Token dataType = null;
        if (consume(currToken(tokens), DATA_TYPE, "Walay Data Type at line " + currToken(tokens).getLine())) dataType = currToken(tokens);
        nextToken(tokens);

        List<Statement> declaration = new ArrayList<>();
        boolean expectNext = false;

        if (consume(currToken(tokens), IDENTIFIER, "Walay variable name at line " + currToken(tokens).getLine())) {
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
        }

        if (expectNext) consume(currToken(tokens), IDENTIFIER, "Walay variable name human sa , at line " + currToken(tokens).getLine());

        return declaration;
    }

    private Statement outputStatement(List<Token> tokens){
//        if (currToken(tokens).getType() == COLON) {
            consume(currToken(tokens), COLON, "Walay ':' human sa " + prevToken(tokens) + " at line " + currToken(tokens).getLine());
            nextToken(tokens);
            Expression expr = expression(tokens);
            return new Statement.Output(expr);
//        }
//        throw new RuntimeException("Missing ':' after " + tokens.getFirst());
    }

    private Statement inputStatement(List<Token> tokens){
//        if (currToken(tokens).getType() == COLON) {
        /*
            TODO:
                - modify this code where it will accept multiple variables
                - posibly change the class Input(tokens/variables)
        */
            consume(currToken(tokens), COLON, "Walay ':' human sa " + prevToken(tokens) + " at line " + currToken(tokens).getLine());
            nextToken(tokens);
            Expression expr = expression(tokens);
            return new Statement.Input(expr);
//        }        //sure ka na expression ni dire girl?
//        throw new RuntimeException("Missing ':' after " + tokens.getFirst());
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

//        System.out.println(currToken(tokens));

        Expression expr = logicalOr(tokens);

        if (
                expr instanceof Expression.Variable &&
                currToken(tokens).getType() == ASS_OP
        ){
            Token var = prevToken(tokens);
            nextToken(tokens);
            return new Expression.Assign(var, assignment(tokens));
        }

        return expr;
    }

    private Expression logicalOr(List<Token> tokens){

//        System.out.println(currToken(tokens));

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

//        System.out.println(currToken(tokens));

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

//        System.out.println(currToken(tokens));

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

//        System.out.println(currToken(tokens));

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

//        System.out.println(currToken(tokens));

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

//        System.out.println(currToken(tokens));

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

//        System.out.println(currToken(tokens));

        if (
                currToken(tokens).getType() == ARITH_ADD ||
                currToken(tokens).getType() == ARITH_MINUS ||
                currToken(tokens).getType() == LOG_AND
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            System.out.println(currToken(tokens));
            Expression right = unary(tokens);
            return new Expression.Unary(op, right);
        }

        return primary(tokens);
    }

    private Expression primary(List<Token> tokens){

//        if (currToken(tokens).getType() == COMMENT)
        // aaaa basta comment oiii TTOTT
        // nya naa pay print na expression
        // naa pay dawat na expression
        // note na ang dawat na expression kay dili mag sugod sa expression na method TTOTT
        // intawn variables rana si DAWAT, separated by comma

//        System.out.println( "primary: " + currToken(tokens));

        Expression expr;

        switch (currToken(tokens).getType()){
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

            //variables
            case IDENTIFIER:
                expr = new Expression.Variable(currToken(tokens));
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
