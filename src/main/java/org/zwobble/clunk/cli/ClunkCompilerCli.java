package org.zwobble.clunk.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.zwobble.clunk.Compiler;
import org.zwobble.clunk.backends.python.PythonBackend;
import org.zwobble.clunk.errors.SourceError;

import java.io.IOException;
import java.nio.file.Paths;

public class ClunkCompilerCli {
    public static void main(String[] rawArgs) throws IOException {
        var args = parseArgs(rawArgs);
        try {
            compile(args);
        } catch (SourceError error) {
            System.err.println(error.getSource().describe());
            System.err.println(error.getMessage());
        }
    }

    private static void compile(Namespace args) throws IOException {
        var sourcePath = Paths.get(args.getString("source"));
        var outputPath = Paths.get(args.getString("output"));

        var compiler = new Compiler();
        compiler.compile(sourcePath, outputPath, new PythonBackend());
    }

    private static Namespace parseArgs(String[] args) {
        var parser = ArgumentParsers.newFor("clunkc").build()
            .defaultHelp(true);

        parser.addArgument("source").required(true);
        parser.addArgument("--backend").choices("python").required(true);
        parser.addArgument("-o", "--output").dest("output").required(true);

        try {
            return parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
            throw new RuntimeException(e);
        }
    }
}
