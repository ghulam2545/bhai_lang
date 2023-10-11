package com.ghulam;

import java.util.List;

public class BhaiFunction implements BhaiCallable {
    private final Stmt.Function declaration;

    public BhaiFunction(Stmt.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter Interpreter, List<Object> arguments) {
        Environment environment = new Environment(Interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).token, arguments.get(i));
        }

        try {
            Interpreter.execute_block(declaration.body, environment);
        } catch (BhaiReturn returnValue) {
            return returnValue.value;
        }

        return null;
    }

}
