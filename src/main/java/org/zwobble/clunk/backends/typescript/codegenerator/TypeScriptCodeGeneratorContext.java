package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptImportNode;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TypeScriptCodeGeneratorContext {
    public static TypeScriptCodeGeneratorContext stub() {
        return new TypeScriptCodeGeneratorContext(SubtypeLookup.EMPTY);
    }

    private record Import(String module, String export) {
    }

    private final Set<Import> imports;
    private final SubtypeLookup subtypeLookup;

    public TypeScriptCodeGeneratorContext(SubtypeLookup subtypeLookup) {
        this.subtypeLookup = subtypeLookup;
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

    public List<RecordType> subtypesOf(Type supertype) {
        return subtypeLookup.subtypesOf(supertype);
    }

    public List<Type> supertypesOf(Type subtype) {
        return subtypeLookup.supertypesOf(subtype);
    }
}
