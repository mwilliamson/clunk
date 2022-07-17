package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.backends.java.ast.JavaImportNode;
import org.zwobble.clunk.backends.java.ast.JavaImportStaticNode;
import org.zwobble.clunk.backends.java.ast.JavaImportTypeNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.typechecker.FieldsLookup;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JavaCodeGeneratorContext {
    public static JavaCodeGeneratorContext stub(FieldsLookup fieldsLookup) {
        return new JavaCodeGeneratorContext(JavaTargetConfig.stub(), fieldsLookup, SubtypeLookup.EMPTY);
    }

    public static JavaCodeGeneratorContext stub() {
        return new JavaCodeGeneratorContext(JavaTargetConfig.stub(), FieldsLookup.EMPTY, SubtypeLookup.EMPTY);
    }

    private final JavaTargetConfig config;
    private final FieldsLookup fieldsLookup;
    private final SubtypeLookup subtypeLookup;

    public JavaCodeGeneratorContext(JavaTargetConfig config, FieldsLookup fieldsLookup, SubtypeLookup subtypeLookup) {
        this.config = config;
        this.fieldsLookup = fieldsLookup;
        this.subtypeLookup = subtypeLookup;
    }

    private final Set<JavaImportNode> imports = new LinkedHashSet<>();

    public void addImportStatic(String packageName, String identifier) {
        imports.add(new JavaImportStaticNode(packageName, identifier));
    }

    public void addImportType(String typeName) {
        imports.add(new JavaImportTypeNode(typeName));
    }

    public Set<JavaImportNode> imports() {
        return imports;
    }

    public String packagePrefix() {
        return config.packagePrefix();
    }

    public List<TypedRecordFieldNode> fieldsOf(RecordType recordType) {
        return fieldsLookup.fieldsOf(recordType);
    }

    public List<RecordType> subtypesOf(Type supertype) {
        return subtypeLookup.subtypesOf(supertype);
    }

    public List<Type> supertypesOf(Type subtype) {
        return subtypeLookup.supertypesOf(subtype);
    }
}
