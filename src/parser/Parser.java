package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

import static errors.Sayop.*;
import static lexer.TokenType.*;

public class Parser {
    private final List<List<Token>> lineTokens;
    private int line;
    private int size;
    int indx;
    int cl;

    private final List<Statement> statements;

    public Parser(List<List<Token>> lineTokens) {
        this.lineTokens = lineTokens;
        this.statements = new ArrayList<>();
        this.line = 0;
        this.size = 0;
        this.indx = 0;
        this.cl = 0;
    }

    public List<Statement> parse(){

        if (lineTokens.getFirst().getFirst() != lineTokens.getFirst().getLast() &&
                lineTokens.getFirst().getFirst().getType() != START_PROG)
            throw new SyntaxError(lineTokens.getFirst().getFirst().getLine(),"Expected 'SUGOD' before this line");
        else line = 1;


        if (lineTokens.getLast().getFirst() != lineTokens.getLast().getLast() &&
                lineTokens.getLast().getLast().getType() != END_PROG)
            throw new SyntaxError(lineTokens.getLast().getLast().getLine(), "Expected 'KATAPUSAN' after this line");
        else size = lineTokens.size()-1;

        parseStatements();

        return statements;
    }

    private void parseStatements(){
        List<Token> currLine;

        while (line < size) {
            currLine = lineTokens.get(line);
//            statement(currLine);
            if (
                    currToken(currLine).getType() == IF ||
                    currToken(currLine).getType() == WHILE
            ) controlStruct();
            else {
                statement(currLine);
                line++;
            }

            indx = 0;   //reset to 0 if mana nag read ang usa ka line

            System.out.println("Successful parsed line " + currLine.getFirst().getLine());
            while (cl < statements.size()) {
                System.out.println(new ASTPrinter().printStatement(statements.get(cl)));
                cl++;
            }
            System.out.println();
        }

        System.out.println("------------------ OUTPUT --------------------");
    }

    private void controlStruct(){
            List<Token> curr = lineTokens.get(line);
            int save = line;

            Expression condition;
            Statement thenBlock;

            switch (currToken(curr).getType()){
                case IF:
                    nextToken(curr);
                    condition = boolCondition(curr, prevToken(curr).getValue().toString());
                    Statement elseBlock = null;

                    if (indx == curr.size()-1) {
                        ++line;
                        if (line >= size) throw new SyntaxError(curr.getLast().getLine(), "Walay statement/s human sa condition");
                        curr = lineTokens.get(line);
                        indx = 0;

                        if (currToken(curr).getType() == BLOCK){
                            nextToken(lineTokens.get(line));
                            thenBlock = blockStatements();
                        } else {
                            statement(curr);
                            thenBlock = statements.removeLast();
                            ++line;
                        }

                    } else {
                        statement(curr);
                        thenBlock = statements.removeLast();
                        ++line;
                    }
                    //TODO: handle elseBlock here
                    // tip: keyword KUNG DILI can be treated as an elseBlock
                    // where elseBlock = Statement.IfStatement(condition, thenBlock, elseBlock)
                    // pero kamo nay bahala unsaon hehe
                    statements.add(new Statement.IfStatement(condition, thenBlock, elseBlock));
                    break;
                case WHILE:
                    nextToken(curr);
                    condition = boolCondition(curr, prevToken(curr).getValue().toString());

                    if (indx == curr.size()-1) {
                        ++line;
                        if (line >= size) throw new SyntaxError(curr.getLast().getLine(), "Walay statement/s human sa condition");
                        curr = lineTokens.get(line);
                        indx = 0;

                        if (currToken(curr).getType() == BLOCK){
                            nextToken(lineTokens.get(line));
                            thenBlock = blockStatements();
                        } else {
                            statement(curr);
                            thenBlock = statements.removeLast();
                            ++line;
                        }

                    } else {
                        statement(curr);
                        thenBlock = statements.removeLast();
                        ++line;
                    }
                    statements.add(new Statement.WhileStatement(condition, thenBlock));
                    break;

                // TODO: add other keywords here (e.g. FOR, DO)
            }
        if (line-save == 1) --line;
    }

    private Statement blockStatements(){
        consume(currToken(lineTokens.get(line)), OPEN_BRACES, "Walay '{' human sa PUNDOK keyword");

        List<Statement> stmts = new ArrayList<>();

        if (indx == lineTokens.get(line).size()-1) {
            ++line;
            indx = 0;
        } else nextToken(lineTokens.get(line));

        while(line < size){

            if (currToken(lineTokens.get(line)).getType() == CLOSE_BRACES) break;

            int start = statements.size();

            if (
                    currToken(lineTokens.get(line)).getType() == IF ||
                    currToken(lineTokens.get(line)).getType() == WHILE
            ) controlStruct();
            else {
                statement(lineTokens.get(line));
                ++line;
                indx = 0;

                if (line >= size){
                    throw new SyntaxError(lineTokens.get(line-1).getLast().getLine(),
                            "Walay '}' human sa block statement");
                }
            }
            for (int i = start; i < statements.size(); i++) stmts.add(statements.get(i));
            while (statements.size() > start) statements.removeLast();
        }

        consume(currToken(lineTokens.get(line)), CLOSE_BRACES, "Walay '}' human sa block statement");

        if (indx == lineTokens.get(line).size() - 1) {
            ++line;
            indx = 0;
        } else nextToken(lineTokens.get(line));

        return new Statement.BlockStatement(stmts);
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
        if (consume(currToken(tokens), DATA_TYPE, "Walay Data Type")) dataType = currToken(tokens);
        nextToken(tokens);

        List<Statement> declaration = new ArrayList<>();
        boolean expectNext = false;

        consume(currToken(tokens), IDENTIFIER, "Walay variable name");

        while (currToken(tokens).getType() == IDENTIFIER) {

            Token var = currToken(tokens);
            nextToken(tokens);

            Expression initialzer = null;

            if(currToken(tokens).getType() == ASS_OP){
                nextToken(tokens);
                initialzer = expression(tokens);
            }

            declaration.add(new Statement.VarDeclaration(dataType, var, initialzer));

            if (currToken(tokens).getType() == COMMA) {
                nextToken(tokens);
                expectNext = true;
            } else {
                expectNext = false;
                break;
            }
        }

        if (expectNext) consume(currToken(tokens), IDENTIFIER, "Walay variable name human sa ',' ");

        return declaration;
    }

    private Statement outputStatement(List<Token> tokens){
            consume(currToken(tokens), COLON, "Walay ':' human sa " + prevToken(tokens));
            nextToken(tokens);
            Expression expr = expression(tokens);
            return new Statement.Output(expr);
    }

    private List<Statement> inputStatement(List<Token> tokens){
            consume(currToken(tokens), COLON, "Walay ':' human sa " + prevToken(tokens));

            nextToken(tokens);
            List<Statement> inputs = new ArrayList<>();

//                Expression expr = expression(tokens);
            while (true) {
                consume(currToken(tokens), IDENTIFIER, "Walay variable para input");
                inputs.add(new Statement.Input(currToken(tokens)));
                nextToken(tokens);
                if (currToken(tokens).getType() == COMMA) nextToken(tokens);
                else break;
            }

            return inputs;
    }

    private Expression boolCondition(List<Token> tokens, String keyword){
//        System.out.println(keyword);
        consume(currToken(tokens), ARITH_OPEN_P, "Walay '(' human sa "+ keyword +" keyword");
        nextToken(tokens);
        Expression cond = expression(tokens);
        consume(currToken(tokens), ARITH_CLOSE_P, "Walay ')' human sa condition");
        return cond;
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
            } else throw new SyntaxError(currToken(tokens).getLine(), "Walay variable before '='");
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
            expr = new Expression.Logic(expr, op, right);
        }

        return expr;
    }

    private Expression logicalAnd(List<Token> tokens){

//        Expression expr = equality(tokens);
        Expression expr = concatenation(tokens);

        while (
                currToken(tokens).getType() == LOG_AND
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
//            Expression right = equality(tokens);
            Expression right = concatenation(tokens);
            expr = new Expression.Logic(expr, op, right);
        }

        return expr;
    }

    private Expression concatenation (List<Token> tokens){
        Expression expr = equality(tokens);

        while (currToken(tokens).getType() == CONCAT){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = equality(tokens);
            expr = new Expression.Binary(expr, op, right);
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
            expr = new Expression.Compare(expr, op, right);
        }

        return expr;
    }

    private Expression term (List<Token> tokens){

        Expression expr = factor(tokens);

        while (
                currToken(tokens).getType() == ARITH_ADD    ||
                currToken(tokens).getType() == ARITH_MINUS
//                        ||
//                currToken(tokens).getType() == CONCAT       // para string
        ){
            Token op = currToken(tokens);
            nextToken(tokens);
            Expression right = factor(tokens);
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
            expr = new Expression.Binary(expr, op, right);
        }

        return expr;
    }

    private Expression unary(List<Token> tokens){

        if (
                currToken(tokens).getType() == ARITH_ADD ||
                currToken(tokens).getType() == ARITH_MINUS ||
                currToken(tokens).getType() == LOG_NOT
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

            //escape codes rar
            case BRACKET_OPEN:
                nextToken(tokens);
                if (currToken(tokens).getType() != ESCAPE_CODE) throw new SyntaxError(currToken(tokens).getLine(), "Walay escape code human sa '['");
                Token code = currToken(tokens);
                nextToken(tokens);
                if (currToken(tokens).getType() != BRACKET_CLOSE) throw new SyntaxError(currToken(tokens).getLine(), "Walay closing brackets human sa escape code");
                nextToken(tokens);
                return new Expression.EscapeCode(code);
             //if group sha
            case ARITH_OPEN_P:
                nextToken(tokens);
                expr = expression(tokens);
                if (currToken(tokens).getType() != ARITH_CLOSE_P) throw new SyntaxError(prevToken(tokens).getLine(), "Expected ')' after expression");
                nextToken(tokens);
                return new Expression.Group(expr);

        }
//        return null;
        throw new RuntimeError(currToken(tokens).getLine(), "Expected expression" + currToken(tokens));
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
        if (curr == null) throw new SyntaxError(curr.getLine(), error);
        if (curr.getType() == type) return true;
        throw new SyntaxError(curr.getLine(),error);
    }

}
