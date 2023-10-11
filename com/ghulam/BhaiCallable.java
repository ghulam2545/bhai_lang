package com.ghulam;

import java.util.List;

public interface BhaiCallable {
    int arity();

    Object call(Interpreter Interpreter, List<Object> arguments);
}
