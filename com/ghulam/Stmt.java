package com.ghulam;

import java.util.List;

public abstract class Stmt {
    interface Visitor<E> {
        E visitBlockStmt (Block stmt);
        E visitExpressionStmt (Expression stmt);
        E visitPrintStmt (Print stmt);
        E visitVarStmt (Var stmt);
        E visitIfStmt (If stmt);
        E visitWhileStmt (While stmt);
        E visitFunctionStmt (Function stmt);
        E visitReturnStmt (Return stmt);
    }

    public static class Block extends Stmt {
        final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    public static class Expression extends Stmt {
        final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    public static class Print extends Stmt {
        final Expr expression;

        public Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    public static class Var extends Stmt {
        final Token name;
        final Expr initializer;

        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    public static class If extends Stmt {
        final Expr condition;
        final Stmt then_branch;
        final Stmt else_branch;

        public If(Expr condition, Stmt then_branch, Stmt else_branch) {
            this.condition = condition;
            this.then_branch = then_branch;
            this.else_branch = else_branch;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    public static class While extends Stmt {
        final Expr condition;
        final Stmt body;

        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    public static class Function extends Stmt {
        final Token name;
        final List<Token> params;
        final List<Stmt> body;

        public Function(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }

    public static class Return extends Stmt {
        final Token keyword;
        final Expr value;

        public Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        <E> E accept(Visitor<E> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }

    abstract <E> E accept(Visitor<E> visitor);
}
