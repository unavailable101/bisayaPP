package lexer;

//under this package kay dapat ang nay access ragyud kay under sa lexer na package
//di dapat ma gamit sa other packages (parser and interpreter)
public interface LiteralChecker {
    boolean isLiteral(String lexeme);
    Token addToken(String lexeme);

    class BoolLC implements LiteralChecker{
        @Override
        public boolean isLiteral(String lexeme){   //Accepted values: "OO" and "DILI"
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
        public boolean isLiteral(String lexeme) {
//        return lexeme.startsWith("'") && lexeme.endsWith("'") && lexeme.length()==3;
//        System.out.println("lexeme: " + lexeme + " length: " + lexeme.length());
            if (lexeme.startsWith("'")){
                if (!lexeme.endsWith("'")) throw new IllegalArgumentException("Sayop: kulangan ni og '");
                if (lexeme.length() > 3) throw new IllegalArgumentException("Sayop: dili na ni siya character");
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
        public boolean isLiteral(String lexeme) {
            if (!lexeme.matches(".*\\d.*")) return false;
            if (lexeme.chars().filter(ch -> ch == '.').count() > 1)
                throw new IllegalArgumentException("Invalid decimal literal: Too many decimal points");
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
        public boolean isLiteral(String lexeme) {
            return lexeme.matches("\\d+");
        }

        @Override
        public Token addToken(String lexeme) {
            return new Token(TokenType.INTEGER, Integer.valueOf(lexeme));
        }
    }

    class StringLC implements LiteralChecker{
        @Override
        public boolean isLiteral(String lexeme) {
//        return lexeme.startsWith("\"") && lexeme.endsWith("\"");
            if (lexeme.startsWith("\"")){
                if (!lexeme.endsWith("\"")) throw new IllegalArgumentException("Sayop: kulangan ni og \"");
            } else return false;
            return true;
        }

        @Override
        public Token addToken(String lexeme) {
            return new Token(TokenType.STRING, lexeme);
        }
    }

}
