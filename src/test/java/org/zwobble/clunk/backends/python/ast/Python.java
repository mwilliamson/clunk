package org.zwobble.clunk.backends.python.ast;

public class Python {
    private Python() {

    }

    public static PythonReferenceNode reference(String name) {
        return new PythonReferenceNode(name);
    }
}
