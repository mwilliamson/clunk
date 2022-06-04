package org.zwobble.clunk.backends.java;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGenerator;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.config.ProjectConfig;
import org.zwobble.clunk.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JavaBackend implements Backend {
    private final Logger logger;

    public JavaBackend(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void compile(
        List<TypedNamespaceNode> typedNamespaceNodes,
        Path outputRoot,
        ProjectConfig projectConfig
    ) throws IOException {
        for (var typedNamespaceNode : typedNamespaceNodes) {
            compileNamespace(typedNamespaceNode, outputRoot, projectConfig);
        }
    }

    private void compileNamespace(
        TypedNamespaceNode typedNamespaceNode,
        Path outputRoot,
        ProjectConfig projectConfig
    ) throws IOException {
        var javaConfig = new JavaTargetConfig(projectConfig.target("java"));
        var javaCompilationUnits = JavaCodeGenerator.compileNamespace(typedNamespaceNode, javaConfig);

        for (var javaCompilationUnit : javaCompilationUnits) {
            var codeBuilder = new CodeBuilder();
            JavaSerialiser.serialiseOrdinaryCompilationUnit(javaCompilationUnit, codeBuilder);

            var outputPath = generateOutputPath(outputRoot, javaCompilationUnit);

            Files.createDirectories(outputPath.getParent());
            var outputContents = codeBuilder.toString();
            logger.outputFile(outputPath, outputContents);
            Files.writeString(outputPath, outputContents, StandardCharsets.UTF_8);
        }
    }

    private Path generateOutputPath(Path outputRoot, JavaOrdinaryCompilationUnitNode javaCompilationUnit) {
        var outputPath = outputRoot;
        for (var part : javaCompilationUnit.packageDeclaration().split("\\.")) {
            outputPath = outputPath.resolve(part);
        }
        return outputPath.resolve(javaCompilationUnit.typeDeclaration().name() + ".java");
    }
}
