package org.zwobble.clunk.backends.python.serialiser;

import org.zwobble.clunk.backends.python.ast.PythonReferenceNode;

public class PythonSerialiser {
    public static void serialiseReference(PythonReferenceNode node, StringBuilder builder) {
        builder.append(node.name());
    }
}
