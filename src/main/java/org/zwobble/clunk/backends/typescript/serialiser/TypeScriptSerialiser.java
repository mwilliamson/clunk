package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptReferenceNode;

public class TypeScriptSerialiser {
    public static void serialiseInterfaceDeclaration(TypeScriptInterfaceDeclarationNode node, StringBuilder builder) {
        builder.append("interface ");
        builder.append(node.name());
        builder.append(" {\n");
        for (var field : node.fields()) {
            builder.append("    ");
            builder.append(field.name());
            builder.append(": ");
            serialiseReference(field.type(), builder);
            builder.append(";\n");
        }
        builder.append("}");
    }

    public static void serialiseReference(TypeScriptReferenceNode node, StringBuilder builder) {
        builder.append(node.name());
    }
}
