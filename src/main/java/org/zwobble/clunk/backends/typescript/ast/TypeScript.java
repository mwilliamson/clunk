package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public class TypeScript {
    private TypeScript() {

    }

    public static TypeScriptBoolLiteralNode boolFalse() {
        return new TypeScriptBoolLiteralNode(false);
    }

    public static TypeScriptBoolLiteralNode boolTrue() {
        return new TypeScriptBoolLiteralNode(true);
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

    public static TypeScriptStringLiteralNode string(String value) {
        return new TypeScriptStringLiteralNode(value);
    }
}
