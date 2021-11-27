package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptModuleNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptReferenceNode;

public class TypeScriptSerialiser {
    public static void serialiseInterfaceDeclaration(TypeScriptInterfaceDeclarationNode node, CodeBuilder builder) {
        builder.append("interface ");
        builder.append(node.name());
        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var field : node.fields()) {
            builder.append("readonly ");
            builder.append(field.name());
            builder.append(": ");
            serialiseReference(field.type(), builder);
            builder.append(";");
            builder.newLine();
        }
        builder.dedent();
        builder.append("}");
    }

    public static void serialiseModule(TypeScriptModuleNode node, CodeBuilder builder) {
        var isFirst = true;
        for (var statement : node.statements()) {
            if (!isFirst) {
                builder.newLine();
                builder.newLine();
            }

            serialiseInterfaceDeclaration(statement, builder);

            isFirst = false;
        }
    }

    public static void serialiseReference(TypeScriptReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }
}
