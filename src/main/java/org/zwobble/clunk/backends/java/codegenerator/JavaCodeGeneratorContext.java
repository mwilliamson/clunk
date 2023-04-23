package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaImportNode;
import org.zwobble.clunk.backends.java.ast.JavaImportStaticNode;
import org.zwobble.clunk.backends.java.ast.JavaImportTypeNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.types.*;

import java.util.*;

public class JavaCodeGeneratorContext {
    public static JavaCodeGeneratorContext stub(SubtypeRelations subtypeRelations) {
        return new JavaCodeGeneratorContext(JavaTargetConfig.stub(), subtypeRelations);
    }

    public static JavaCodeGeneratorContext stub() {
        return new JavaCodeGeneratorContext(JavaTargetConfig.stub(), SubtypeRelations.EMPTY);
    }

    private final JavaTargetConfig config;
    private final LinkedHashSet<JavaImportNode> imports;
    private final SubtypeRelations subtypeRelations;
    private final Map<String, String> variableRenames;

    public JavaCodeGeneratorContext(JavaTargetConfig config, SubtypeRelations subtypeRelations) {
        this.config = config;
        this.imports = new LinkedHashSet<>();
        this.subtypeRelations = subtypeRelations;
        this.variableRenames = new HashMap<>();
    }

    public JavaCodeGeneratorContext(
        JavaTargetConfig config,
        LinkedHashSet<JavaImportNode> imports,
        SubtypeRelations subtypeRelations,
        Map<String, String> variableRenames
    ) {
        this.config = config;
        this.imports = imports;
        this.subtypeRelations = subtypeRelations;
        this.variableRenames = variableRenames;
    }

    public JavaCodeGeneratorContext enterCompilationUnit() {
        return new JavaCodeGeneratorContext(
            config,
            new LinkedHashSet<>(imports),
            subtypeRelations,
            new HashMap<>(variableRenames)
        );
    }

    public JavaCodeGeneratorContext enterBlock() {
        return new JavaCodeGeneratorContext(
            config,
            imports,
            subtypeRelations,
            new HashMap<>(variableRenames)
        );
    }

    public void addImportStatic(String packageName, String identifier) {
        imports.add(new JavaImportStaticNode(packageName, identifier));
    }

    public void addImportType(String typeName) {
        imports.add(new JavaImportTypeNode(typeName));
    }

    public void addImports(List<JavaImportNode> imports) {
        this.imports.addAll(imports);
    }

    public Set<JavaImportNode> imports() {
        return imports;
    }

    public List<JavaImportNode> generateImports() {
        return imports.stream().toList();
    }

    public String packagePrefix() {
        return config.packagePrefix();
    }

    public List<RecordType> sealedInterfaceCases(InterfaceType sealedInterfaceType) {
        return subtypeRelations.sealedInterfaceCases(sealedInterfaceType);
    }

    public boolean isInterfaceType(Type type) {
        return type instanceof InterfaceType;
    }

    public List<StructuredType> extendedTypes(Type subtype) {
        return subtypeRelations.extendedTypes(subtype);
    }

    public void renameVariable(String from, String to) {
        variableRenames.put(from, to);
    }

    public String variableName(String from) {
        return variableRenames.getOrDefault(from, from);
    }
}
