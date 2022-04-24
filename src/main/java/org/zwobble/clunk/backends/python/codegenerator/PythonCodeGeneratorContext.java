package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.backends.python.ast.PythonImportFromNode;
import org.zwobble.clunk.backends.python.ast.PythonImportNode;
import org.zwobble.clunk.backends.python.ast.PythonStatementNode;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PythonCodeGeneratorContext {
    public static PythonCodeGeneratorContext stub() {
        return new PythonCodeGeneratorContext();
    }

    private final Set<List<String>> imports;

    public PythonCodeGeneratorContext() {
        this.imports = new LinkedHashSet<>();
    }

    public void addImport(List<String> import_) {
        imports.add(import_);
    }

    public List<PythonStatementNode> imports() {
        return imports.stream()
            .map(import_ -> generateImport(import_))
            .toList();
    }

    private PythonStatementNode generateImport(List<String> import_) {
        if (import_.size() == 1) {
            return new PythonImportNode(String.join(".", import_));
        } else {
            return new PythonImportFromNode(
                String.join(".", import_.subList(0, import_.size() - 1)),
                List.of(import_.get(import_.size() - 1))
            );
        }
    }
}
