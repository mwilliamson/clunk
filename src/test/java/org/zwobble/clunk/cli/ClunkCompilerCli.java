package org.zwobble.clunk.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.parser.Parser;
import org.zwobble.clunk.parser.Tokeniser;
import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.typechecker.TypeChecker;
import org.zwobble.clunk.typechecker.TypeCheckerContext;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

        var sourceContents = Files.readString(sourcePath);
        var source = FileFragmentSource.create(sourcePath.toString(), sourceContents);
        var tokens = Tokeniser.tokenise(source);
        var parser = new Parser(source);
        var untypedNamespaceNode = parser.parseNamespaceName(tokens, NamespaceName.parts("x"));

        // TODO: proper typing for builtin modules
        var typeCheckerContext = TypeCheckerContext.EMPTY
            .updateNamespaceType(new NamespaceType(NamespaceName.parts("stdlib", "assertions"), Map.ofEntries(
                Map.entry("assertThat", new StaticFunctionType(
                    NamespaceName.parts("stdlib", "assertions"),
                    "assertThat",
                    List.of(Types.OBJECT, Types.UNIT),
                    Types.UNIT
                ))
            )))
            .updateNamespaceType(new NamespaceType(NamespaceName.parts("stdlib", "matchers"), Map.ofEntries(
                Map.entry("equalTo", new StaticFunctionType(
                    NamespaceName.parts("stdlib", "matchers"),
                    "equalTo",
                    List.of(Types.OBJECT),
                    Types.UNIT
                ))
            )))
            .withEnvironment(Builtins.ENVIRONMENT);
        var typedNamespaceNode = TypeChecker.typeCheckNamespace(untypedNamespaceNode, typeCheckerContext);

        var pythonModule = PythonCodeGenerator.DEFAULT.compileNamespace(typedNamespaceNode);
        var codeBuilder = new CodeBuilder();
        PythonSerialiser.serialiseModule(pythonModule, codeBuilder);
        Files.writeString(outputPath, codeBuilder.toString(), StandardCharsets.UTF_8);
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
