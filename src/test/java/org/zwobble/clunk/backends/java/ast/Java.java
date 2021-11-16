package org.zwobble.clunk.backends.java.ast;

public class Java {
    public static JavaTypeReferenceNode typeReference(String name) {
        return new JavaTypeReferenceNode(name);
    }
}
