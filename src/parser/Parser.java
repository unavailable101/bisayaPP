package parser;

import lexer.Token;

import java.util.List;

public class Parser {
    //naa na dire ang laay
    //kapoy
    private List<List<Token>> tokens;
    private int start;

    public Parser(List<List<Token>> tokens) {
        this.tokens = tokens;
        this.start = -1;
    }
}
