import com.ghulam.Runner;

public class App {
    public static void main(String[] args) throws Exception {
        // AstGenerator.main(args);

        if (args.length < 1) {
            System.err.println("ERROR: Please provide an input source file.");
            System.exit(1);
        } else if (args.length == 1) {

            // check for .bhai file is omitted
            String source_file = args[0];
            Runner.run_source_file(source_file);
        } else {
            Runner.repl_mode();
        }
    }
}

