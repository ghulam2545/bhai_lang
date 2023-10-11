package com.ghulam;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.token)) {
            values.put(name.token, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, " undefined variable " + name.token + ".");
    }

    public Object get(Token name) {
        if (values.containsKey(name.token))
            return values.get(name.token);

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, " undefined variable " + name.token + ".");
    }
}
