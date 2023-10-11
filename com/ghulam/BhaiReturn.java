package com.ghulam;

public class BhaiReturn extends RuntimeException {
    final Object value;

    BhaiReturn(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
