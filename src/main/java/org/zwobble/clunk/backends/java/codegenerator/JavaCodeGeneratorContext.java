package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaImportNode;
import org.zwobble.clunk.backends.java.ast.JavaImportStaticNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JavaCodeGeneratorContext {
    public static JavaCodeGeneratorContext stub() {
        return new JavaCodeGeneratorContext(JavaTargetConfig.stub(), SubtypeLookup.EMPTY);
    }

    private final JavaTargetConfig config;
    private final SubtypeLookup subtypeLookup;

    public JavaCodeGeneratorContext(JavaTargetConfig config, SubtypeLookup subtypeLookup) {
        this.config = config;
        this.subtypeLookup = subtypeLookup;
    }

    private final Set<JavaImportNode> imports = new LinkedHashSet<>();

    public void addImportStatic(String packageName, String identifier) {
        imports.add(new JavaImportStaticNode(packageName, identifier));
    }

    public Set<JavaImportNode> imports() {
        return imports;
    }

    public String packagePrefix() {
        return config.packagePrefix();
    }

    public List<RecordType> subtypesOf(Type supertype) {
        return subtypeLookup.subtypesOf(supertype);
    }

    public List<Type> supertypesOf(Type subtype) {
        return subtypeLookup.supertypesOf(subtype);
    }
}
