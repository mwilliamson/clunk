package org.zwobble.clunk.backends.typescript.ast;

public class TypeScript {
    private TypeScript() {

    }

    public static TypeScriptInterfaceFieldNode interfaceField(String name, TypeScriptReferenceNode type) {
        return new TypeScriptInterfaceFieldNode(name, type);
    }

    public static TypeScriptReferenceNode reference(String name) {
        return new TypeScriptReferenceNode(name);
    }
}
