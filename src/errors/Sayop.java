package errors;

public class Sayop extends RuntimeException {
    private final int line;
    private final String message;

    public Sayop(int line, String message) {
        super(message);
        this.line = line;
        this.message = message;
    }

    @Override
    public String toString() {
        return line + " : " + message;
    }

    public static class LexicalError extends Sayop{
        public LexicalError(int line, String message) {
            super(line, "Lexical Error: " + message);
        }
    }

    public static class SyntaxError extends Sayop{
        public SyntaxError(int line, String message) {
            super(line, "Syntax Error: " + message);
        }
    }

    public static class TypeError extends Sayop {
        public TypeError(int line, String message) {
            super(line, "TypeError: " + message);
        }
    }

    public static class RuntimeError extends Sayop{
        public RuntimeError(int line, String message) {
            super(line, "RuntimeError: " + message);
        }
    }

    public static class UndefinedVariableError extends RuntimeError{
        public UndefinedVariableError(int line, String message) {
            super(line, "Wala na ilhi na variable '" + message + "'");
        }
    }


}

