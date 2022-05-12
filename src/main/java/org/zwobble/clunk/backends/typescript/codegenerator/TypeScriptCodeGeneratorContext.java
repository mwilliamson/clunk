package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGeneratorContext;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptImportNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptStatementNode;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TypeScriptCodeGeneratorContext {
    public static TypeScriptCodeGeneratorContext stub() {
        return new TypeScriptCodeGeneratorContext();
    }

    private record Import(String module, String export) {
    }

    private final Set<Import> imports;

    public TypeScriptCodeGeneratorContext() {
        this.imports = new LinkedHashSet<>();
    }

    public void addImport(String module, String export) {
        imports.add(new Import(module, export));
    }

    public List<TypeScriptImportNode> imports() {
        return imports.stream()
            .map(import_ -> new TypeScriptImportNode(import_.module(), List.of(import_.export())))
            .toList();
    }
}
