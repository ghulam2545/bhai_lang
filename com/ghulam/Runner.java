package com.ghulam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import static com.ghulam.TokenType.EOF_TOKEN;

public class Runner {
    private static final Interpreter Interpreter = new Interpreter();
    private static boolean some_error = false;
    private static boolean runtime_error = false;

    public static void run_source_file(String source_file) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(source_file));
            String source = new String(bytes, Charset.defaultCharset());

            // lookup the code
            // System.out.println(source);
            // System.out.println("-------------------------------------------");

            run_code(source);

            // ??
            if (some_error)
                System.exit(1);
            if (runtime_error)
                System.exit(1);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void repl_mode() {
        System.out.println("REPL Mode::");

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print(">> ");
            try {
                String line = reader.readLine();
                if (line == null || line.equals("exit") || line.equals("exit."))
                    break;

                run_code(line);

                // don't terminate repl session for any error
                some_error = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void run_code(String source) {
        Scanner scanner = new Scanner(source);
        var tokens = scanner.scan_tokens();
        // System.out.println("count of tokens: " + tokens.size());

        Parser parser = new Parser(tokens);
        var parsed_out = parser.parse();

        // for (var e : tokens)
        // System.out.println(e);

        if (some_error) {
            System.out.println("\nTerminating the prog.");
            return;
        }

        Interpreter.interpret(parsed_out);
    }

    public static void error(int line, String msg) {
        report(line, "", msg);
    }

    public static void error(Token token, String msg) {
        if (token.type == EOF_TOKEN)
            report(token.line, " at the end", msg);
        else
            report(token.line, " at '" + token.token + "'", msg);
    }

    public static void runtime_error(RuntimeError error) {
        System.err.println("runtime error: " + error.getMessage());
        runtime_error = true;
    }

    private static void report(int line, String where, String msg) {
        System.err.println("Error at [ line " + line + " ]: " + where + ".");
        System.out.println("Message: " + msg);
        some_error = true;
        System.exit(1); // ??
    }
}
