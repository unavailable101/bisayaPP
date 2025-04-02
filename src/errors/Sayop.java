package errors;

public class Sayop extends RuntimeException {
    private final int line;
    private final String message;
    private final String codeLine;
    private final int position;

    static {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (e instanceof Sayop) System.err.println(e);
                else {
                    System.err.println("Unexpected error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // kani, plano nako ano, para ma butngan position ang error like
    // example
    /*
        MUGNA NUMERO a = 1,
                          ^
    */
    // naaay comma pero walau sumpay. another example:
    /*
        x = ( ( 4 + 5 )
                       ^
    */
    // missing parenthesis
    // pero lage, kapoy
    //if you want to continue this, goodluck finding the positition hehe

    public Sayop(int line, String message, String codeLine, int position) {
        super(message);
        this.line = line;
        this.message = message;
        this.codeLine = codeLine;
        this.position = position;
    }

    public Sayop(int line, String message) {
        this (line, message, null, -1);
    }

    @Override
    public String toString() {
        StringBuilder error = new StringBuilder();
        error.append("Line " + line + " : " + message);

        if (codeLine != null && position >= 0){
            error.append("\n" + codeLine + "\n");
            error.append(" ".repeat(position)).append("^");
        }

        return error.toString();
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