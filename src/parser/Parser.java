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
        // literals
        if (
                currToken(tokens).getType() == TokenType.INTEGER    ||
                currToken(tokens).getType() == TokenType.DOUBLE     ||
                currToken(tokens).getType() == TokenType.CHARACTERS ||
                currToken(tokens).getType() == TokenType.STRING     ||
                currToken(tokens).getType() == TokenType.BOOLEAN
        ) return new Expression.Literal(currToken(tokens));


        // identifiers/variables
        if (currToken(tokens).getType() == TokenType.IDENTIFIER){
            if (indx < tokens.size()){
                nextToken(tokens);
                if (currToken(tokens).getType() == TokenType.ASS_OP) return new Expression.Assign(prevToken(tokens), expression(tokens));
                if (
                        currToken(tokens).getType() == TokenType.ARITH_GT       ||
                        currToken(tokens).getType() == TokenType.ARITH_LT       ||
                        currToken(tokens).getType() == TokenType.ARITH_GOE      ||
                        currToken(tokens).getType() == TokenType.ARITH_LOE      ||
                        currToken(tokens).getType() == TokenType.ARITH_EQUAL    ||
                        currToken(tokens).getType() == TokenType.ARITH_NOT_EQUAL
                ) return new Expression.Compare(expression(tokens), currToken(tokens), expression(tokens));
                if (
                        currToken(tokens).getType() == TokenType.LOG_OR     ||
                        currToken(tokens).getType() == TokenType.LOG_NOT    ||
                        currToken(tokens).getType() == TokenType.LOG_AND
                ) return new Expression.Logic(expression(tokens), currToken(tokens), expression(tokens));

                if (
                        currToken(tokens).getType() == TokenType.ARITH_ADD      ||
                        currToken(tokens).getType() == TokenType.ARITH_MINUS    ||
                        currToken(tokens).getType() == TokenType.ARITH_DIV      ||
                        currToken(tokens).getType() == TokenType.ARITH_MULT     ||
                        currToken(tokens).getType() == TokenType.ARITH_MOD
                ) return new Expression.Binary(expression(tokens), currToken(tokens), expression(tokens));

            }
            return new Expression.Variable(currToken(tokens));
        }

        if (
                currToken(tokens).getType() == TokenType.ARITH_ADD ||
                currToken(tokens).getType() == TokenType.ARITH_MINUS
        ){
            return new Expression.Unary(currToken(tokens), expression(tokens));
        }

        return null;
    }

    private Token nextToken (List<Token> tokens){
        return indx < tokens.size() ? tokens.get(indx+1) : tokens.get(tokens.size()-1);
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
