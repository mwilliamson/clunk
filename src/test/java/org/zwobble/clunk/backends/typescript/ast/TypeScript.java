package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public class TypeScript {
    private TypeScript() {

    }

    public static TypeScriptInterfaceFieldNode interfaceField(String name, TypeScriptReferenceNode type) {
        return new TypeScriptInterfaceFieldNode(name, type);
    }

    public static TypeScriptModuleNode module(String name, List<TypeScriptInterfaceDeclarationNode> statements) {
        return new TypeScriptModuleNode(name, statements);
    }

    public static TypeScriptReferenceNode reference(String name) {
        return new TypeScriptReferenceNode(name);
    }
}