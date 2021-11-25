package org.zwobble.clunk.backends.java.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeReferenceNode;

public class JavaSerialiser {
    public static void serialiseOrdinaryCompilationUnit(JavaOrdinaryCompilationUnitNode node, CodeBuilder builder) {
        serialisePackageDeclaration(node.packageDeclaration(), builder);
        builder.newLine();
        builder.newLine();
        serialiseRecordDeclaration(node.typeDeclaration(), builder);
    }

    private static void serialisePackageDeclaration(String packageDeclaration, CodeBuilder builder) {
        builder.append("package ");
        builder.append(packageDeclaration);
        builder.append(";");
    }

    public static void serialiseRecordDeclaration(JavaRecordDeclarationNode node, CodeBuilder builder) {
        builder.append("public record ");
        builder.append(node.name());
        builder.append("(");

        var first = true;
        for (var component : node.components()) {
            if (!first) {
                builder.append(", ");
            }
            serialiseTypeReference(component.type(), builder);
            builder.append(" ");
            builder.append(component.name());

            first = false;
        }

        builder.append(") {\n}");
    }

    public static void serialiseTypeReference(JavaTypeReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }
}
