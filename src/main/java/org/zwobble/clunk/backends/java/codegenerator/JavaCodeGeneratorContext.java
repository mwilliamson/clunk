package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.backends.java.ast.JavaImportNode;
import org.zwobble.clunk.backends.java.ast.JavaImportStaticNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;

import java.util.LinkedHashSet;
import java.util.Set;

public class JavaCodeGeneratorContext {
    public static JavaCodeGeneratorContext stub() {
        return new JavaCodeGeneratorContext(JavaTargetConfig.stub());
    }

    private final JavaTargetConfig config;

    public JavaCodeGeneratorContext(JavaTargetConfig config) {
        this.config = config;
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
}
