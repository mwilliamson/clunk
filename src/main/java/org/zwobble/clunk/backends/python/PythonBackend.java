package org.zwobble.clunk.backends.python;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.NamespaceName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonBackend implements Backend {
    @Override
    public void compile(TypedNamespaceNode typedNamespaceNode, Path outputRoot) throws IOException {
        var pythonModule = PythonCodeGenerator.DEFAULT.compileNamespace(typedNamespaceNode);
        var codeBuilder = new CodeBuilder();
        PythonSerialiser.serialiseModule(pythonModule, codeBuilder);

        var outputPath = generateOutputPath(outputRoot, typedNamespaceNode.name());

        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, codeBuilder.toString(), StandardCharsets.UTF_8);
    }

    private Path generateOutputPath(Path outputRoot, NamespaceName namespaceName) {
        var outputPath = outputRoot;
        for (var part : namespaceName.parts()) {
            outputPath = outputPath.resolve(part);
        }
        return outputPath.resolveSibling(outputPath.getFileName().toString() + ".py");
    }
}
