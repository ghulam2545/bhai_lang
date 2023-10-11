package com.ghulam;

public enum TokenType {
    // braces
    LEFT_PAREN_TOKEN,
    RIGHT_PAREN_TOKEN,
    LEFT_CURLY_TOKEN,
    RIGHT_CURLY_TOKEN,

    // arithmetic ops
    PLUS_OP_TOKEN,
    MINUS_OP_TOKEN,
    STAR_OP_TOKEN,
    SLASH_OP_TOKEN,

    COMMA_TOKEN,
    SEMICOLON_TOKEN,

    // comparison
    EQUAL_TOKEN,
    EQUAL_EQUAL_TOKEN,
    LESS_TOKEN,
    LESS_EQUAL_TOKEN,
    GREATER_TOKEN,
    GREATER_EQUAL_TOKEN,
    BANG_TOKEN,
    BANG_EQUAL_TOKEN,

    // boolean
    TRUE_TOKEN,
    FALSE_TOKEN,

    // control flow
    IF_TOKEN,
    ELSE_TOKEN,
    FOR_TOKEN,
    WHILE_TOKEN,

    // logical
    AND_TOKEN,
    OR_TOKEN,

    PRINT_TOKEN,
    IDENTIFIER_TOKEN, // name of the variable
    STRING_TOKEN,
    NUMBER_TOKEN,
    FUNCTION_TOKEN,
    RETURN_TOKEN,

    VAR_TOKEN, // var keyword
    NULL_TOKEN,

    LINE_BREAK,

    // end of file, yay!
    EOF_TOKEN,
}
