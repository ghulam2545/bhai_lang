package com.ghulam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.ghulam.TokenType.*;

public class Parser {
    private final List<Token> tokens;
    private int current_pos = 0;

    public static class ParseError extends RuntimeException {
    }

    private ParseError error(Token token, String message) {
        Runner.error(token, message);
        return new ParseError();
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statments = new ArrayList<>();
        try {
            while (!end_of_file()) {
                statments.add(declaration());
                // statments.add(statement());
            }
        } catch (ParseError error) {
            return null; // ??
        }

        return statments;
    }

    private Expr expression() {
        return assignment();
        // return equality();
    }

    private Expr assignment() {
        // Expr expr = equality();
        Expr expr = or();

        if (match(EQUAL_TOKEN)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, " invalid assignment target");
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(EQUAL_EQUAL_TOKEN, BANG_EQUAL_TOKEN)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(LESS_TOKEN, LESS_EQUAL_TOKEN, GREATER_TOKEN, GREATER_EQUAL_TOKEN)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS_OP_TOKEN, PLUS_OP_TOKEN)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH_OP_TOKEN, STAR_OP_TOKEN)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG_TOKEN, MINUS_OP_TOKEN)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        // return primary();
        return call();
    }

    private Expr primary() {
        if (match(TRUE_TOKEN))
            return new Expr.Literal(true);
        if (match(FALSE_TOKEN))
            return new Expr.Literal(false);
        if (match(NULL_TOKEN))
            return new Expr.Literal(null);
        if (match(LINE_BREAK))
            return new Expr.Literal('\n');

        if (match(IDENTIFIER_TOKEN))
            return new Expr.Variable(previous());

        if (match(NUMBER_TOKEN, STRING_TOKEN))
            return new Expr.Literal(previous().literal);

        if (match(LEFT_PAREN_TOKEN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN_TOKEN, " expected ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), " expected expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();
        throw error(peek(), message);
    }

    private Stmt statement() {
        if (match(FOR_TOKEN))
            return for_statement();
        if (match(IF_TOKEN))
            return if_statement();
        if (match(WHILE_TOKEN))
            return while_statement();
        if (match(PRINT_TOKEN))
            return print_statement();
        if (match(RETURN_TOKEN))
            return return_statement();
        if (match(LEFT_CURLY_TOKEN))
            return new Stmt.Block(block());

        return expression_statement();
    }

    private Stmt print_statement() {
        Expr value = expression();
        consume(SEMICOLON_TOKEN, " expected ';' after value.");

        return new Stmt.Print(value);
    }

    private Stmt expression_statement() {
        Expr expr = expression();
        consume(SEMICOLON_TOKEN, " expected ';' after expression.");

        return new Stmt.Expression(expr);
    }

    private Stmt declaration() {
        try {
            if (match(FUNCTION_TOKEN))
                return function("func");
            if (match(VAR_TOKEN))
                return var_declaration();

            return statement();
        } catch (ParseError error) {
            error.printStackTrace(); // ??
            return null;
        }
    }

    private Stmt var_declaration() {
        Token name = consume(IDENTIFIER_TOKEN, " expected a variable name");

        Expr initializer = null;
        if (match(EQUAL_TOKEN))
            initializer = expression();

        consume(SEMICOLON_TOKEN, " expected ';' after variable declaration");
        return new Stmt.Var(name, initializer);
    }

    private List<Stmt> block() {
        List<Stmt> stmts = new ArrayList<>();
        while (!check(RIGHT_CURLY_TOKEN) && !end_of_file()) {
            stmts.add(declaration());
        }

        consume(RIGHT_CURLY_TOKEN, " expected '}' brace afetr block");
        return stmts;
    }

    private Stmt if_statement() {
        consume(LEFT_PAREN_TOKEN, " expect '(' after 'if'");
        Expr condition = expression();
        consume(RIGHT_PAREN_TOKEN, " expect ')' after if condition");
        Stmt then_branch = statement();
        Stmt else_branch = null;
        if (match(ELSE_TOKEN)) {
            else_branch = statement();
        }

        return new Stmt.If(condition, then_branch, else_branch);
    }

    private Expr or() {
        Expr expr = and();
        while (match(OR_TOKEN)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Expr and() {
        Expr expr = equality();
        while (match(AND_TOKEN)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    private Stmt while_statement() {
        consume(LEFT_PAREN_TOKEN, " expect '(' after 'while'");
        Expr condition = expression();
        consume(RIGHT_PAREN_TOKEN, " expect ')' after condition");
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    private Stmt for_statement() {
        consume(LEFT_PAREN_TOKEN, " expect '(' after 'for'");

        Stmt initializer;
        if (match(SEMICOLON_TOKEN)) {
            initializer = null;
        } else if (match(VAR_TOKEN)) {
            initializer = var_declaration();
        } else {
            initializer = expression_statement();
        }

        Expr condition = null;
        if (!check(SEMICOLON_TOKEN)) {
            condition = expression();
        }
        consume(SEMICOLON_TOKEN, " expect ';' after loop condition");

        Expr increment = null;
        if (!check(RIGHT_PAREN_TOKEN)) {
            increment = expression();
        }
        consume(RIGHT_PAREN_TOKEN, "Expect ')' after for clauses.");

        Stmt body = statement();
        if (increment != null) {
            body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
        }
        if (condition == null)
            condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));

        }
        return body;
    }

    private Expr call() {
        Expr expr = primary();
        while (true) {
            if (match(LEFT_PAREN_TOKEN)) {
                expr = finish_call(expr);
            } else {
                break;
            }
        }
        return expr;
    }

    private Expr finish_call(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(RIGHT_PAREN_TOKEN)) {
            do {
                if (arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }

                arguments.add(expression());
            } while (match(COMMA_TOKEN));
        }
        Token paren = consume(RIGHT_PAREN_TOKEN, " expect ')' after arguments");

        return new Expr.Call(callee, paren, arguments);
    }

    private Stmt.Function function(String kind) {
        Token name = consume(IDENTIFIER_TOKEN, "Expect " + kind + " name.");
        consume(LEFT_PAREN_TOKEN, "Expect '(' after " + kind + " name.");
        List<Token> parameters = new ArrayList<>();
        if (!check(RIGHT_PAREN_TOKEN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }
                parameters.add(
                        consume(IDENTIFIER_TOKEN, "Expect parameter name."));
            } while (match(COMMA_TOKEN));
        }
        consume(RIGHT_PAREN_TOKEN, "Expect ')' after parameters.");

        consume(LEFT_CURLY_TOKEN, "Expect '{' before " + kind + " body.");
        List<Stmt> body = block();
        return new Stmt.Function(name, parameters, body);
    }

    private Stmt return_statement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(SEMICOLON_TOKEN)) {
            value = expression();
        }
        consume(SEMICOLON_TOKEN, " expect ';' after return value.");

        return new Stmt.Return(keyword, value);
    }

    // helper
    private boolean end_of_file() {
        return peek().type.equals(EOF_TOKEN);
    }

    // return current token
    private Token peek() {
        return tokens.get(current_pos);
    }

    // consume token
    private Token advance() {
        if (!end_of_file())
            ++current_pos;
        return previous();
    }

    // return token at currrent_pos - 1;
    private Token previous() {
        return tokens.get(current_pos - 1);
    }

    // check if these tokens are matching to list of TokenType
    private boolean match(TokenType... types) {
        for (var type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    // check if this token equal to peek's token
    private boolean check(TokenType type) {
        if (end_of_file())
            return false;
        return peek().type == type;
    }
}
