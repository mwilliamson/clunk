package org.zwobble.clunk.backends.java.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.*;

import static org.zwobble.clunk.util.Iterables.forEachInterspersed;

public class JavaSerialiser {
    private static void serialiseBoolLiteral(JavaBoolLiteralNode node, CodeBuilder builder) {
        builder.append(node.value() ? "true" : "false");
    }

    private static void serialiseClassDeclaration(JavaClassDeclarationNode node, CodeBuilder builder) {
        builder.append("public class ");
        builder.append(node.name());
        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var bodyDeclaration : node.body()) {
            serialiseClassBodyDeclaration(bodyDeclaration, builder);
        }
        builder.dedent();
        builder.append("}");
    }

    private static void serialiseClassBodyDeclaration(JavaClassBodyDeclarationNode node, CodeBuilder builder) {
        node.accept(new JavaClassBodyDeclarationNode.Visitor<Void>() {
            @Override
            public Void visit(JavaMethodDeclarationNode node) {
                serialiseMethodDeclaration(node, builder);
                return null;
            }
        });
    }

    public static void serialiseExpression(JavaExpressionNode node, CodeBuilder builder) {
        node.accept(new JavaExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(JavaBoolLiteralNode node) {
                serialiseBoolLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaStringLiteralNode node) {
                serialiseStringLiteral(node, builder);
                return null;
            }
        });
    }

    public static void serialiseMethodDeclaration(JavaMethodDeclarationNode node, CodeBuilder builder) {
        builder.append("public ");
        serialiseTypeExpression(node.returnType(), builder);
        builder.append(" ");
        builder.append(node.name());
        builder.append("(");
        forEachInterspersed(
            node.params(),
            param -> serialiseParam(param, builder),
            () -> builder.append(", ")
        );
        builder.append(") {");
        builder.newLine();
        builder.append("}");
        builder.newLine();
    }

    public static void serialiseOrdinaryCompilationUnit(JavaOrdinaryCompilationUnitNode node, CodeBuilder builder) {
        serialisePackageDeclaration(node.packageDeclaration(), builder);
        builder.newLine();
        builder.newLine();
        serialiseTypeDeclaration(node.typeDeclaration(), builder);
    }

    private static void serialisePackageDeclaration(String packageDeclaration, CodeBuilder builder) {
        builder.append("package ");
        builder.append(packageDeclaration);
        builder.append(";");
    }

    private static void serialiseParam(JavaParamNode param, CodeBuilder builder) {
        serialiseTypeExpression(param.type(), builder);
        builder.append(" ");
        builder.append(param.name());
    }

    private static void serialiseRecordDeclaration(JavaRecordDeclarationNode node, CodeBuilder builder) {
        builder.append("public record ");
        builder.append(node.name());
        builder.append("(");

        forEachInterspersed(
            node.components(),
            component -> {
                serialiseTypeExpression(component.type(), builder);
                builder.append(" ");
                builder.append(component.name());
            },
            () -> builder.append(", ")
        );

        builder.append(") {\n}");
    }

    private static void serialiseStringLiteral(JavaStringLiteralNode node, CodeBuilder builder) {
        builder.append("\"");
        var escapedValue = node.value()
            .replace("\\", "\\\\")
            .replace("\b", "\\b")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\f", "\\f")
            .replace("\r", "\\r")
            .replace("\"", "\\\"");
        builder.append(escapedValue);
        builder.append("\"");
    }

    public static void serialiseTypeDeclaration(JavaTypeDeclarationNode node, CodeBuilder builder) {
        node.accept(new JavaTypeDeclarationNode.Visitor<Void>() {
            @Override
            public Void visit(JavaClassDeclarationNode node) {
                serialiseClassDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaRecordDeclarationNode node) {
                serialiseRecordDeclaration(node, builder);
                return null;
            }
        });
    }

    public static void serialiseTypeExpression(JavaTypeExpressionNode node, CodeBuilder builder) {
        node.accept(new JavaTypeExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(JavaTypeReferenceNode node) {
                serialiseTypeReference(node, builder);
                return null;
            }
        });
    }

    private static void serialiseTypeReference(JavaTypeReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }
}
