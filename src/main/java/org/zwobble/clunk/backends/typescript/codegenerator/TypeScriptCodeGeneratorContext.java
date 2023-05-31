package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptImportNamedMemberNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptImportNamedNode;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.StructuredType;
import org.zwobble.clunk.types.SubtypeRelations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TypeScriptCodeGeneratorContext {
    public static TypeScriptCodeGeneratorContext stub() {
        return new TypeScriptCodeGeneratorContext(SubtypeRelations.EMPTY);
    }

    private record Import(String module, String export, String importName) {
    }

    private final Set<Import> imports;
    private final SubtypeRelations subtypeRelations;

    public TypeScriptCodeGeneratorContext(SubtypeRelations subtypeRelations) {
        this.subtypeRelations = subtypeRelations;
        this.imports = new LinkedHashSet<>();
    }

    public void addImport(String module, String export) {
        addImport(module, export, export);
    }

    public void addImport(String module, String export, String importName) {
        imports.add(new Import(module, export, importName));
    }

    public List<TypeScriptImportNamedNode> imports() {
        return imports.stream()
            .map(import_ -> new TypeScriptImportNamedNode(
                import_.module(),
                List.of(new TypeScriptImportNamedMemberNode(import_.export(), import_.importName()))
            ))
            .toList();
    }

    public List<StructuredType> sealedInterfaceCases(InterfaceType supertype) {
        return subtypeRelations.sealedInterfaceCases(supertype);
    }
}
