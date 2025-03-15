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
        List<Token> tokens;

        for (int i = 1; i < size; i++) {
            statements.add(statement(lineTokens.get(i)));
            indx = 0;   //reset to 0 if mana nag read ang usa ka line
        }

        return statements;
    }

    private Statement statement(List<Token> tokens){
        switch (tokens.get(0).getType()){
            case TokenType.OUTPUT: return outputStatement(tokens);
            case TokenType.INPUT: return inputStatement(tokens);
            case TokenType.VAR_DECLARATION: return varDeclare(tokens);
            default: return exprStatement(tokens);
        }
    }

    private Statement varDeclare(List<Token> tokens){
        Token dataType = consume(currToken(tokens), TokenType.DATA_TYPE, "Walay Data Type");
        Expression initialzer = null;

        return new Statement.VarDeclaration(dataType, initialzer);
    }

    private Statement outputStatement(List<Token> tokens){
        if (currToken(tokens).getType() == TokenType.COLON)  return new Statement.Output(expression(tokens));
        throw new RuntimeException("Missing ':' after " + tokens.getFirst());
    }

    private Statement inputStatement(List<Token> tokens){
        if (currToken(tokens).getType() == TokenType.COLON)  return new Statement.Input(expression(tokens));
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
        return null;
    }

    private Expression unary (List<Token> tokens){
        Token isSymbol = currToken(tokens);
        if (check(isSymbol, TokenType.ARITH_ADD) || check(isSymbol, TokenType.ARITH_MINUS))
        return new Expression.Unary(isSymbol, expression(tokens));
        return null;    // ambot sakto bha ni
    }

    private Token currToken (List<Token> tokens){
        return indx+1  > tokens.size() ? tokens.get(++indx) : tokens.get(indx);
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
