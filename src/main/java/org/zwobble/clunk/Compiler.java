package org.zwobble.clunk;

import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.parser.Parser;
import org.zwobble.clunk.parser.Tokeniser;
import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.typechecker.TypeChecker;
import org.zwobble.clunk.types.NamespaceName;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public void compile(Path projectPath, Path outputRoot, Backend backend) throws IOException {
        var sourceRoot = projectPath.resolve("src");
        var sourcePaths = collectSourceFiles(sourceRoot);
        for (var sourcePath : sourcePaths) {
            var outputFilename = sourcePath.getFileName().toString().replaceAll("\\.clunk$", ".py");
            var outputPath = outputRoot.resolve(sourceRoot.relativize(sourcePath).resolveSibling(outputFilename));
            System.out.println(outputPath);
            compileFile(sourcePath, outputPath, backend);
        }
    }

    private List<Path> collectSourceFiles(Path sourceRoot) throws IOException {
        var paths = new ArrayList<Path>();
        Files.walkFileTree(sourceRoot, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(
                Path file,
                BasicFileAttributes attrs
            ) {
                if (file.getFileName().toString().endsWith(".clunk")) {
                    paths.add(file);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                Path dir,
                IOException exc
            ) {
                return FileVisitResult.CONTINUE;
            }
        });
        return paths;
    }

    private void compileFile(Path sourcePath, Path outputPath, Backend backend) throws IOException {
        var sourceContents = Files.readString(sourcePath);
        var source = FileFragmentSource.create(sourcePath.toString(), sourceContents);
        var tokens = Tokeniser.tokenise(source);
        var parser = new Parser(source);
        var untypedNamespaceNode = parser.parseNamespaceName(tokens, NamespaceName.fromParts("x"));

        var typedNamespaceNode = TypeChecker.typeCheckNamespace(untypedNamespaceNode, Builtins.TYPE_CHECKER_CONTEXT);

        backend.compile(typedNamespaceNode, outputPath);
    }
}
