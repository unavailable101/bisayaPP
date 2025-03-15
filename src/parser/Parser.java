package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

// hello tree, my worst nightmare
// we meet again
// akala ko graduate na ako sayo, pero hindi pa pala

public class Parser {
    //naa na dire ang laay
    //kapoy
    private final List<List<Token>> lineTokens;
    private int line;

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
        return null;
    }

    private Statement outputStatement(List<Token> tokens){
        return null;
    }

    private Statement inputStatement(List<Token> tokens){
        return null;
    }

    private Statement exprStatement (List<Token> tokens){
        return null;
    }
}
