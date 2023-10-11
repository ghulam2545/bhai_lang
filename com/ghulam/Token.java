package com.ghulam;

public class Token {
    final TokenType type;
    final String token; // lexeme
    final Object literal; // number and string literals
    final int line;

    public Token(TokenType tye, String token, Object literal, int line) {
        this.type = tye;
        this.token = token;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return String.format("%-25s %-10s %-20s", type, token, literal);
    }
}
