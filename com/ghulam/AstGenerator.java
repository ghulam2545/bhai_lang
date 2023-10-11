package com.ghulam;

import java.io.PrintWriter;
import java.util.List;

public class AstGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("ERROR: no outpur dir is specified.");
            System.exit(1);
        }

        String output_dir = args[0];

        var expr = List.of(
                "Grouping : Expr expression",
                "Literal : Object value",
                "Variable : Token name",
                "Assign : Token name, Expr value",
                "Logical : Expr left, Token operator, Expr right",
                "Unary : Token operator, Expr right",
                "Binary : Expr left, Token operator, Expr right",
                "Call : Expr callee, Token paren, List<Expr> arguments");

        var stmt = List.of(
                "Block : List<Stmt> statements",
                "Expression : Expr expression",
                "Print : Expr expression",
                "Var : Token name, Expr initializer",
                "If : Expr condition, Stmt then_branch, Stmt else_branch",
                "While : Expr condition, Stmt body",
                "Function : Token name, List<Token> params, List<Stmt> body",
                "Return : Token keyword, Expr value");

        define_ast(output_dir, "Expr", expr);
        define_ast(output_dir, "Stmt", stmt);

        System.out.println("<< Expr.java >> file is generated.");
        System.out.println("<< Stmt.java >> file is generated.");
    }

    private static void define_ast(String output_dir, String parent_class, List<String> types)
            throws Exception {
        String path = output_dir + "/" + parent_class + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.ghulam;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();

        writer.println("public abstract class " + parent_class + " {");
        define_visitor(writer, parent_class, types);

        for (var type : types) {
            String class_name = type.split(":")[0].trim();
            String field = type.split(":")[1].trim();
            define_type(writer, parent_class, class_name, field);
        }

        writer.println("    abstract <E> E accept(Visitor<E> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void define_visitor(PrintWriter writer, String parent_class, List<String> types) {
        writer.println("    interface Visitor<E> {");
        for (var type : types) {
            String type_name = type.split(":")[0].trim();
            writer.println(
                    "        E visit" + type_name + parent_class + " (" + type_name + " " + parent_class.toLowerCase()
                            + ");");
        }
        writer.println("    }\n");
    }

    private static void define_type(PrintWriter writer, String parent_class, String class_name, String field_list) {
        writer.println("    public static class " + class_name + " extends " + parent_class + " {");

        // fields
        String[] fields = field_list.split(", ");
        for (var field : fields) {
            writer.println("        final " + field + ";");
        }
        writer.println();

        // constructor
        writer.println("        public " + class_name + "(" + field_list + ") {");
        for (var field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }
        writer.println("        }");

        // override accept method
        writer.println("\n        @Override");
        writer.println("        <E> E accept(Visitor<E> visitor) {");
        writer.println("            return visitor.visit" + class_name + parent_class + "(this);");
        writer.println("        }");

        writer.println("    }\n");

    }

}
