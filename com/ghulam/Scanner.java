package com.ghulam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.ghulam.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start_pos = 0;
    private int current_pos = 0;
    private int line = 1;

    private static Map<String, TokenType> keywords;

    // do this only one time for entire program/session
    static {
        keywords = new HashMap<>();

        keywords.put("sahi", TRUE_TOKEN);
        keywords.put("galat", FALSE_TOKEN);

        keywords.put("agar_bhai", IF_TOKEN);
        keywords.put("warna_bhai", ELSE_TOKEN);
        keywords.put("chalao_bhai", FOR_TOKEN);
        keywords.put("jab_tak_bhai", WHILE_TOKEN);

        keywords.put("and", AND_TOKEN);
        keywords.put("or", OR_TOKEN);

        keywords.put("bol_bhai", PRINT_TOKEN);
        keywords.put("karna_bhai", FUNCTION_TOKEN);
        keywords.put("lauta_bhai", RETURN_TOKEN);

        keywords.put("bhai_ye_hai", VAR_TOKEN);
        keywords.put("nalla", NULL_TOKEN);

        keywords.put("line_break", LINE_BREAK);
    }

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scan_tokens() {
        while (!end_of_file()) {
            start_pos = current_pos;
            scan_token();
        }

        // add last token for file end
        tokens.add(new Token(EOF_TOKEN, "", null, line));

        return tokens;
    }

    // token matching mechanism goes here
    private void scan_token() {
        char ch = advance();
        switch (ch) {
            case '(':
                add_token(LEFT_PAREN_TOKEN);
                break;
            case ')':
                add_token(RIGHT_PAREN_TOKEN);
                break;
            case '{':
                add_token(LEFT_CURLY_TOKEN);
                break;
            case '}':
                add_token(RIGHT_CURLY_TOKEN);
                break;
            case ',':
                add_token(COMMA_TOKEN);
                break;
            case ';':
                add_token(SEMICOLON_TOKEN);
                break;
            case '+':
                add_token(PLUS_OP_TOKEN);
                break;
            case '-':
                add_token(MINUS_OP_TOKEN);
                break;
            case '*':
                add_token(STAR_OP_TOKEN);
                break;
            case '/':
                if (next_match('/')) {
                    while (peek() != '\r' && !end_of_file()) // ??
                        advance();
                } else {
                    add_token(SLASH_OP_TOKEN);
                }
                break;
            case '<':
                add_token(next_match('=') ? LESS_EQUAL_TOKEN : LESS_TOKEN);
                break;
            case '>':
                add_token(next_match('=') ? GREATER_EQUAL_TOKEN : GREATER_TOKEN);
                break;
            case '=':
                add_token(next_match('=') ? EQUAL_EQUAL_TOKEN : EQUAL_TOKEN);
                break;
            case '!':
                add_token(next_match('=') ? BANG_EQUAL_TOKEN : BANG_TOKEN);
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                ++line;
                break;
            case '"':
                check_for_string();
                break;

            default: {
                if (is_digit(ch)) {
                    check_for_number();
                } else if (is_alpha(ch)) {
                    check_for_identifier();
                } else {
                    System.err.println("Line: " + line + ", : unexpected character."); // ?
                }
                break;
            }
        }
    }

    /**********************************
     * Helper mothods
     * 
     */

    // add this token to list of tokens
    private void add_token(TokenType type) {
        add_token(type, null);
    }

    // add this token to list of tokens with number or string literal
    private void add_token(TokenType type, Object literal) {
        String token = source.substring(start_pos, current_pos);
        tokens.add(new Token(type, token, literal, line));
    }

    // in double quote
    private void check_for_string() {
        while (peek() != '"' && !end_of_file()) {
            if (peek() == '\n')
                ++line;
            advance();
        }

        if (end_of_file()) {
            Runner.error(line, " string is not terminated."); // ??
            return;
        }

        // consume closing quote
        advance();

        String str_val = source.substring(start_pos + 1, current_pos - 1);
        add_token(STRING_TOKEN, str_val);
    }

    // we only support double numbers
    private void check_for_number() {
        while (is_digit(peek()))
            advance();

        if (peek() == '.' && is_digit(next_peek())) {
            advance(); // consume dot ".";

            while (is_digit(peek()))
                advance();
        }

        String num_val = source.substring(start_pos, current_pos);
        add_token(NUMBER_TOKEN, Double.parseDouble(num_val));
    }

    // identifier also refered as variable name
    private void check_for_identifier() {
        while (is_alpha_numeric(peek()))
            advance();

        String key = source.substring(start_pos, current_pos);
        var token = keywords.get(key);

        if (token == null)
            token = IDENTIFIER_TOKEN;

        add_token(token);
    }

    // check if we are at the end of file
    private boolean end_of_file() {
        return current_pos >= source.length();
    }

    // consumes the next character in the source file and return it
    private char advance() {
        ++current_pos;
        return source.charAt(current_pos - 1);
    }

    // match the next character is the same as `expected`
    private boolean next_match(char expected) {
        if (end_of_file())
            return false;
        if (source.charAt(current_pos) != expected)
            return false;

        ++current_pos;
        return true;
    }

    // return character at current position, don't not consume
    private char peek() {
        if (end_of_file())
            return '\0';
        return source.charAt(current_pos);
    }

    // return character at current position + 1, don't not consume
    private char next_peek() {
        if (current_pos + 1 >= source.length())
            return '\0';
        return source.charAt(current_pos + 1);
    }

    // only numbers from 0-9
    private boolean is_digit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    // alphabets and underscore only
    private boolean is_alpha(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
    }

    // consist of alphabets and digits and underscore only
    private boolean is_alpha_numeric(char ch) {
        return is_alpha(ch) || is_digit(ch);
    }
}
