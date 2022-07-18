package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptImportNode;
import org.zwobble.clunk.typechecker.FieldsLookup;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TypeScriptCodeGeneratorContext {
    public static TypeScriptCodeGeneratorContext stub(FieldsLookup fieldsLookup) {
        return new TypeScriptCodeGeneratorContext(fieldsLookup, SubtypeLookup.EMPTY);
    }

    public static TypeScriptCodeGeneratorContext stub() {
        return new TypeScriptCodeGeneratorContext(FieldsLookup.EMPTY, SubtypeLookup.EMPTY);
    }

    private record Import(String module, String export) {
    }

    private final Set<Import> imports;
    private final FieldsLookup fieldsLookup;
    private final SubtypeLookup subtypeLookup;

    public TypeScriptCodeGeneratorContext(FieldsLookup fieldsLookup, SubtypeLookup subtypeLookup) {
        this.fieldsLookup = fieldsLookup;
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

    public List<TypedRecordFieldNode> fieldsOf(RecordType recordType) {
        return fieldsLookup.fieldsOf(recordType);
    }

    public List<RecordType> subtypesOf(Type supertype) {
        return subtypeLookup.subtypesOf(supertype);
    }

    public List<InterfaceType> supertypesOf(Type subtype) {
        return subtypeLookup.supertypesOf(subtype);
    }
}
