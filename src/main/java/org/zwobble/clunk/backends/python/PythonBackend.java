package org.zwobble.clunk.backends.python;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.config.ProjectConfig;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.typechecker.TypeCheckResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PythonBackend implements Backend {
    private final Logger logger;

    public PythonBackend(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void compile(
        TypeCheckResult<List<TypedNamespaceNode>> typeCheckResult,
        Path outputRoot,
        ProjectConfig projectConfig
    ) throws IOException {
        for (var typedNamespaceNode : typeCheckResult.typedNode()) {
            compileNamespace(typedNamespaceNode, outputRoot);
        }
        Files.writeString(outputRoot.resolve("tox.ini"), "[pytest]\npython_files = *_test.py\npython_classes = *Tests\n");
    }

    private void compileNamespace(TypedNamespaceNode typedNamespaceNode, Path outputRoot) throws IOException {
        var pythonModule = PythonCodeGenerator.compileNamespace(typedNamespaceNode);
        var codeBuilder = new CodeBuilder();
        PythonSerialiser.serialiseModule(pythonModule, codeBuilder);

        var outputPath = generateOutputPath(outputRoot, typedNamespaceNode);

        Files.createDirectories(outputPath.getParent());
        var outputContents = "from __future__ import annotations\n\n" + codeBuilder.toString();
        logger.outputFile(outputPath, outputContents);
        Files.writeString(outputPath, outputContents, StandardCharsets.UTF_8);
    }

    private Path generateOutputPath(Path outputRoot, TypedNamespaceNode namespaceNode) {
        var outputPath = outputRoot;
        for (var part : namespaceNode.id().name().parts()) {
            outputPath = outputPath.resolve(part);
        }
        var testSuffix = namespaceNode.id().sourceType() == SourceType.TEST ? "_test" : "";
        return outputPath.resolveSibling(outputPath.getFileName().toString() + testSuffix + ".py");
    }
}
