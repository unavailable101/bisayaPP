package lexer;

import errors.Sayop;

import static errors.Sayop.*;

//under this package kay dapat ang nay access ragyud kay under sa lexer na package
//di dapat ma gamit sa other packages (parser and interpreter)
interface LiteralChecker {
    boolean isLiteral(String lexemem, int line);
    Token addToken(String lexeme);

    class BoolLC implements LiteralChecker{
        @Override
        public boolean isLiteral(String lexeme, int line){   //Accepted values: "OO" and "DILI"
            return lexeme.matches("^\"(OO|DILI)\"$");
//        if (lexeme.startsWith("\"")){
//            if (!lexeme.endsWith("\"")) throw new IllegalArgumentException("Sayop: kulangan ni og \"");
//        }
//        return lexeme.substring(1,lexeme.length()-1).equals("OO") || lexeme.substring(1,lexeme.length()-1).equals("DILI");
        }

        @Override
        public Token addToken(String lexeme) {
            return new Token(TokenType.BOOLEAN, lexeme);
        }
    }

    class CharLC implements LiteralChecker{
        @Override
        public boolean isLiteral(String lexeme, int line) {
//        return lexeme.startsWith("'") && lexeme.endsWith("'") && lexeme.length()==3;
//        System.out.println("lexeme: " + lexeme + " length: " + lexeme.length());
            if (lexeme.startsWith("'")){
                if (!lexeme.endsWith("'")) throw new LexicalError(line,"Kulangan og ' ang character");
                if (lexeme.length() > 3) throw new LexicalError(line, "Dili na ni character");
            } else return false;
            return true;
        }

        @Override
        public Token addToken(String lexeme) {
            return new Token(TokenType.CHARACTERS, lexeme);
        }
    }

    class DoubleLC implements LiteralChecker {
        @Override
        public boolean isLiteral(String lexeme, int line) {
            if (!lexeme.matches(".*\\d.*")) return false;
            if (lexeme.chars().filter(ch -> ch == '.').count() > 1)
                throw new LexicalError(line, "Sobraan og tuldok ang tipik");
            if (!lexeme.matches("\\d*\\.\\d+")) return false;
            return true;
        }

        @Override
        public Token addToken(String lexeme) {
            return new Token(TokenType.DOUBLE, Double.valueOf(lexeme));
        }
    }

    class IntLC implements LiteralChecker{
        @Override
        public boolean isLiteral(String lexeme, int line) {
            return lexeme.matches("\\d+");
        }

        @Override
        public Token addToken(String lexeme) {
            return new Token(TokenType.INTEGER, Integer.valueOf(lexeme));
        }
    }

    class StringLC implements LiteralChecker{
        @Override
        public boolean isLiteral(String lexeme, int line) {
//        return lexeme.startsWith("\"") && lexeme.endsWith("\"");
            if (lexeme.startsWith("\"")){
                if (!lexeme.endsWith("\"")) throw new LexicalError(line, "Kulangan og '\"' ang pisi");
            } else return false;
            return true;
        }

        @Override
        public Token addToken(String lexeme) {
            return new Token(TokenType.STRING, lexeme);
        }
    }

}
