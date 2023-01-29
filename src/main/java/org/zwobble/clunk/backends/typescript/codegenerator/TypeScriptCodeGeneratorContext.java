package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptImportNamedNode;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.SubtypeRelations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TypeScriptCodeGeneratorContext {
    public static TypeScriptCodeGeneratorContext stub() {
        return new TypeScriptCodeGeneratorContext(SubtypeRelations.EMPTY);
    }

    private record Import(String module, String export) {
    }

    private final Set<Import> imports;
    private final SubtypeRelations subtypeRelations;

    public TypeScriptCodeGeneratorContext(SubtypeRelations subtypeRelations) {
        this.subtypeRelations = subtypeRelations;
        this.imports = new LinkedHashSet<>();
    }

    public void addImport(String module, String export) {
        imports.add(new Import(module, export));
    }

    public List<TypeScriptImportNamedNode> imports() {
        return imports.stream()
            .map(import_ -> new TypeScriptImportNamedNode(import_.module(), List.of(import_.export())))
            .toList();
    }

    public List<RecordType> sealedInterfaceCases(InterfaceType supertype) {
        return subtypeRelations.sealedInterfaceCases(supertype);
    }
}
