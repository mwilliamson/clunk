package org.zwobble.clunk.backends.python;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PythonBackend implements Backend {
    @Override
    public void compile(TypedNamespaceNode typedNamespaceNode, Path outputPath) throws IOException {
        var pythonModule = PythonCodeGenerator.DEFAULT.compileNamespace(typedNamespaceNode);
        var codeBuilder = new CodeBuilder();
        PythonSerialiser.serialiseModule(pythonModule, codeBuilder);
        Files.writeString(outputPath, codeBuilder.toString(), StandardCharsets.UTF_8);
    }
}
