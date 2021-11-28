package org.zwobble.clunk.backends.java.ast;

public class Java {
    public static JavaStringLiteralNode string(String value) {
        return new JavaStringLiteralNode(value);
    }

    public static JavaTypeReferenceNode typeReference(String name) {
        return new JavaTypeReferenceNode(name);
    }
}
