package com.ghulam;

import java.util.ArrayList;
import java.util.List;

import com.ghulam.Expr.Assign;
import com.ghulam.Expr.Binary;
import com.ghulam.Expr.Call;
import com.ghulam.Expr.Grouping;
import com.ghulam.Expr.Literal;
import com.ghulam.Expr.Logical;
import com.ghulam.Expr.Unary;
import com.ghulam.Expr.Variable;
import com.ghulam.Stmt.Block;
import com.ghulam.Stmt.Expression;
import com.ghulam.Stmt.Function;
import com.ghulam.Stmt.If;
import com.ghulam.Stmt.Print;
import com.ghulam.Stmt.Return;
import com.ghulam.Stmt.Var;
import com.ghulam.Stmt.While;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;

    public void interpret(List<Stmt> stmts) {
        try {
            for (var e : stmts)
                e.accept(this);
        } catch (RuntimeError error) {
            // System.out.println(error);
            Runner.runtime_error(error);
        }
    }

    private String stringfy(Object object) {
        if (object == null)
            return "nalla";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        execute_block(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.print(stringfy(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;

        if (stmt.initializer != null)
            value = evaluate(stmt.initializer);

        environment.define(stmt.name.token, value);
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        if (is_truthy(evaluate(stmt.condition))) {
            execute(stmt.then_branch);
        } else if (stmt.else_branch != null) {
            execute(stmt.else_branch);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        while (is_truthy(evaluate(stmt.condition))) {
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        BhaiFunction function = new BhaiFunction(stmt);
        environment.define(stmt.name.token, function);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        Object value = null;
        if (stmt.value != null)
            value = evaluate(stmt.value);
        throw new BhaiReturn(value);
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);

        return value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
        Object left = evaluate(expr.left);
        if (expr.operator.type == TokenType.OR_TOKEN) {
            if (is_truthy(left))
                return left;
        } else {
            if (!is_truthy(left))
                return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        var token_type = expr.operator.type;
        switch (token_type) {
            case BANG_TOKEN:
                return !is_truthy(right);
            case MINUS_OP_TOKEN:
                return -(double) right;

            default:
                return null;
        }
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        var token_type = expr.operator.type;
        switch (token_type) {
            case MINUS_OP_TOKEN:
                return (double) left - (double) right;
            case PLUS_OP_TOKEN: {
                if (left instanceof Double && right instanceof Double)
                    return (double) left + (double) right;

                if (left instanceof String && right instanceof Double)
                    return (String) left + String.valueOf(right);

                if (left instanceof Double && right instanceof String)
                    return String.valueOf(left) + (String) right;

                if (left instanceof String && right instanceof String)
                    return (String) left + (String) right;

            }
            case STAR_OP_TOKEN:
                return (double) left * (double) right;
            case SLASH_OP_TOKEN:
                return (double) left / (double) right;
            case EQUAL_EQUAL_TOKEN:
                return is_equal(left, right);
            case BANG_EQUAL_TOKEN:
                return !is_equal(left, right);
            case LESS_TOKEN:
                return (double) left < (double) right;
            case LESS_EQUAL_TOKEN:
                return (double) left <= (double) right;
            case GREATER_TOKEN:
                return (double) left > (double) right;
            case GREATER_EQUAL_TOKEN:
                return (double) left >= (double) right;

            default:
                return null;
        }
    }

    @Override
    public Object visitCallExpr(Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof BhaiCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions");
        }

        BhaiCallable function = (BhaiCallable) callee;

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return function.call(this, arguments);
    }

    // helper
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void execute_block(List<Stmt> stmts, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt stmt : stmts) {
                execute(stmt);
            }
        } finally {
            this.environment = previous;
        }
    }

    private boolean is_truthy(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Boolean)
            return (boolean) obj;
        return true;
    }

    private boolean is_equal(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return true;
        if (o1 == null)
            return false;

        return o1.equals(o2);
    }
}
