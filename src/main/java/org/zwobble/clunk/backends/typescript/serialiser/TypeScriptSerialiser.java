package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptModuleNode;
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

    public static void serialiseModule(TypeScriptModuleNode node, StringBuilder builder) {
        var isFirst = true;
        for (var statement : node.statements()) {
            if (!isFirst) {
                builder.append("\n\n");
            }

            serialiseInterfaceDeclaration(statement, builder);

            isFirst = false;
        }
    }

    public static void serialiseReference(TypeScriptReferenceNode node, StringBuilder builder) {
        builder.append(node.name());
    }
}
