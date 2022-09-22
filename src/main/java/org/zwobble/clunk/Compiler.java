package org.zwobble.clunk;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.config.ProjectConfig;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.parser.Parser;
import org.zwobble.clunk.parser.Tokeniser;
import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.typechecker.TypeCheckResult;
import org.zwobble.clunk.typechecker.TypeChecker;
import org.zwobble.clunk.typechecker.TypeCheckerContext;
import org.zwobble.clunk.types.NamespaceName;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Compiler {
    private final Logger logger;

    public Compiler(Logger logger) {
        this.logger = logger;
    }

    public void compile(Path projectPath, Path outputRoot, Backend backend) throws IOException {
        var projectConfig = ProjectConfig.read(projectPath);

        var sourceRoot = projectPath.resolve("src");
        var sourcePaths = collectSourceFiles(sourceRoot);

        var typedNamespaceNodes = new ArrayList<TypedNamespaceNode>();
        var typeCheckerContext = Builtins.TYPE_CHECKER_CONTEXT;
        for (var sourcePath : sourcePaths) {
            var sourcePathInfo = sourcePathInfo(sourceRoot.relativize(sourcePath));
            var result = readFile(sourcePath, sourcePathInfo, typeCheckerContext);
            typedNamespaceNodes.add(result.typedNode());
            typeCheckerContext = result.context();
        }

        backend.compile(new TypeCheckResult<>(typedNamespaceNodes, typeCheckerContext), outputRoot, projectConfig);
    }

    private record SourcePathInfo(NamespaceName namespaceName, SourceType sourceType) {
    }

    private SourcePathInfo sourcePathInfo(Path sourcePath) {
        // TODO: tidy this up! and test it
        var fileNameParts = sourcePath.getFileName().toString().split("\\.");
        var namespaceParts = sourcePath.resolveSibling(fileNameParts[0]);
        var namespaceName = new NamespaceName(pathToParts(namespaceParts));
        if (fileNameParts.length == 2) {
            return new SourcePathInfo(namespaceName, SourceType.SOURCE);
        } else if (fileNameParts.length == 3 && fileNameParts[1].equals("test")) {
            return new SourcePathInfo(namespaceName, SourceType.TEST);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private List<String> pathToParts(Path namespaceParts) {
        var result = new ArrayList<String>();
        for (var i = 0; i < namespaceParts.getNameCount(); i++) {
            result.add(namespaceParts.getName(i).toString());
        }
        return result;
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
        paths.sort(Comparator.naturalOrder());
        return paths;
    }

    private TypeCheckResult<TypedNamespaceNode> readFile(
        Path sourcePath,
        SourcePathInfo sourcePathInfo,
        TypeCheckerContext context
    ) throws IOException {
        var sourceContents = Files.readString(sourcePath);
        logger.sourceFile(sourcePath, sourceContents);
        var source = FileFragmentSource.create(sourcePath.toString(), sourceContents);
        var tokens = Tokeniser.tokenise(source);
        var parser = new Parser();
        var untypedNamespaceNode = parser.parseNamespace(tokens, sourcePathInfo.namespaceName, sourcePathInfo.sourceType);

        return TypeChecker.typeCheckNamespace(untypedNamespaceNode, context);
    }
}
