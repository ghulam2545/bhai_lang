package com.ghulam;

import java.util.List;

public abstract class Expr {
    interface Visitor<E> {
        E visitGroupingExpr (Grouping expr);
        E visitLiteralExpr (Literal expr);
        E visitVariableExpr (Variable expr);
        E visitAssignExpr (Assign expr);
        E visitLogicalExpr (Logical expr);
        E visitUnaryExpr (Unary expr);
        E visitBinaryExpr (Binary expr);
        E visitCallExpr (Call expr);
    }

    public static class Grouping extends Expr {
        final Expr expression;

        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    public static class Literal extends Expr {
        final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    public static class Variable extends Expr {
        final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    public static class Assign extends Expr {
        final Token name;
        final Expr value;

        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    public static class Logical extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        public Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    public static class Unary extends Expr {
        final Token operator;
        final Expr right;

        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    public static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    public static class Call extends Expr {
        final Expr callee;
        final Token paren;
        final List<Expr> arguments;

        public Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    abstract <E> E accept(Visitor<E> visitor);
}
