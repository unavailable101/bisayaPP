package interpreter;

import errors.Sayop;
import errors.Sayop.UndefinedVariableError;
import lexer.Token;

import java.util.HashMap;
import java.util.Map;

class Environment {
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, Token> types = new HashMap<>();
    final Environment enclosing;

    public Environment() {
        this.enclosing = null;
    }

    public Environment(Environment enclosing){
        this.enclosing = enclosing;
    }

    Token getType(String var) {
        if (types.containsKey(var)) {
            return types.get(var);
        }

        if (enclosing != null) {
            return enclosing.getType(var);
        }

        throw new RuntimeException("Undefined variable '" + var + "'");
    }

    Object get(Token name){
        if (values.containsKey(name.getValue())) return values.get(name.getValue());

        if (enclosing != null) return enclosing.get(name); // search in parent

        throw new UndefinedVariableError(name.getLine(), name.getValue().toString());
    }


    void define (Token type, String name, Object value){
        values.put(name, value);
        types.put(name, type);
    }
    void assign (Token name, Object value){
        if (values.containsKey(name.getValue().toString())){
            values.put(name.getValue().toString(), value);
            return;
        }

        if (enclosing != null){
            enclosing.assign(name, value);
            return;
        }

        throw new UndefinedVariableError(name.getLine(),name.getValue().toString() );
    }


}
