package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaImportNode;
import org.zwobble.clunk.backends.java.ast.JavaImportStaticNode;
import org.zwobble.clunk.backends.java.ast.JavaImportTypeNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.types.SubtypeRelations;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JavaCodeGeneratorContext {
    public static JavaCodeGeneratorContext stub() {
        return new JavaCodeGeneratorContext(JavaTargetConfig.stub(), SubtypeRelations.EMPTY);
    }

    private final JavaTargetConfig config;
    private final SubtypeRelations subtypeRelations;

    public JavaCodeGeneratorContext(JavaTargetConfig config, SubtypeRelations subtypeRelations) {
        this.config = config;
        this.subtypeRelations = subtypeRelations;
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

    public List<RecordType> subtypesOf(Type supertype) {
        return subtypeRelations.subtypesOf(supertype);
    }
}
