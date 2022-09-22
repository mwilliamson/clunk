package org.zwobble.clunk.backends.typescript;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGenerator;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.config.ProjectConfig;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.typechecker.TypeCheckResult;
import org.zwobble.clunk.types.SubtypeRelations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TypeScriptBackend implements Backend {
    private final Logger logger;

    public TypeScriptBackend(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void compile(
        TypeCheckResult<List<TypedNamespaceNode>> typeCheckResult,
        Path outputRoot,
        ProjectConfig projectConfig
    ) throws IOException {
        var subtypeRelations = typeCheckResult.context().subtypeRelations();

        for (var typedNamespaceNode : typeCheckResult.typedNode()) {
            compileNamespace(typedNamespaceNode, outputRoot, subtypeRelations);
        }
    }

    private void compileNamespace(TypedNamespaceNode typedNamespaceNode, Path outputRoot, SubtypeRelations subtypeRelations) throws IOException {
        var typeScriptModule = TypeScriptCodeGenerator.compileNamespace(typedNamespaceNode, subtypeRelations);
        var codeBuilder = new CodeBuilder();
        TypeScriptSerialiser.serialiseModule(typeScriptModule, codeBuilder);

        var outputPath = generateOutputPath(outputRoot, typedNamespaceNode);

        Files.createDirectories(outputPath.getParent());
        var outputContents = codeBuilder.toString();
        logger.outputFile(outputPath, outputContents);
        Files.writeString(outputPath, outputContents, StandardCharsets.UTF_8);
    }

    private Path generateOutputPath(Path outputRoot, TypedNamespaceNode namespaceNode) {
        var outputPath = outputRoot;
        for (var part : namespaceNode.name().parts()) {
            outputPath = outputPath.resolve(part);
        }
        var testSuffix = namespaceNode.sourceType() == SourceType.TEST ? ".test" : "";
        return outputPath.resolveSibling(outputPath.getFileName().toString() + testSuffix + ".ts");
    }
}
