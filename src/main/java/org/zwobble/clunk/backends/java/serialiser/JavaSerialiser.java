package org.zwobble.clunk.backends.java.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.*;

import static org.zwobble.clunk.util.Iterables.forEachInterspersed;

public class JavaSerialiser {
    public static void serialiseAnnotation(JavaAnnotationNode node, CodeBuilder builder) {
        node.accept(new JavaAnnotationNode.Visitor<Void>() {
            @Override
            public Void visit(JavaMarkerAnnotationNode node) {
                builder.append("@");
                serialiseTypeExpression(node.type(), builder);
                return null;
            }

            @Override
            public Void visit(JavaSingleElementAnnotation node) {
                builder.append("@");
                serialiseTypeExpression(node.type(), builder);
                builder.append("(");
                serialiseExpression(node.value(), builder);
                builder.append(")");
                return null;
            }
        });
    }

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

    public static void serialiseClassBodyDeclaration(JavaClassBodyDeclarationNode node, CodeBuilder builder) {
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
            public Void visit(JavaReferenceNode node) {
                serialiseReference(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaStringLiteralNode node) {
                serialiseStringLiteral(node, builder);
                return null;
            }
        });
    }

    private static void serialiseFullyQualifiedTypeReference(
        JavaFullyQualifiedTypeReferenceNode node,
        CodeBuilder builder
    ) {
        builder.append(node.packageName());
        builder.append(".");
        builder.append(node.typeName());
    }

    public static void serialiseMethodDeclaration(JavaMethodDeclarationNode node, CodeBuilder builder) {
        for (var annotation : node.annotations()) {
            serialiseAnnotation(annotation, builder);
            builder.newLine();
        }

        builder.append("public ");
        if (node.isStatic()) {
            builder.append("static ");
        }
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
        builder.indent();
        for (var bodyStatement : node.body()) {
            serialiseStatement(bodyStatement, builder);
        }
        builder.dedent();
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

    private static void serialiseReference(JavaReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    private static void serialiseReturn(JavaReturnNode node, CodeBuilder builder) {
        builder.append("return ");
        serialiseExpression(node.expression(), builder);
        builder.append(";");
        builder.newLine();
    }

    public static void serialiseStatement(JavaStatementNode node, CodeBuilder builder) {
        node.accept(new JavaStatementNode.Visitor<Void>() {
            @Override
            public Void visit(JavaVariableDeclarationNode node) {
                serialiseVariableDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaReturnNode node) {
                serialiseReturn(node, builder);
                return null;
            }
        });
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
            public Void visit(JavaFullyQualifiedTypeReferenceNode node) {
                serialiseFullyQualifiedTypeReference(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaTypeVariableReferenceNode node) {
                serialiseTypeVariableReference(node, builder);
                return null;
            }
        });
    }

    private static void serialiseTypeVariableReference(JavaTypeVariableReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    private static void serialiseVariableDeclaration(JavaVariableDeclarationNode node, CodeBuilder builder) {
        builder.append("var ");
        builder.append(node.name());
        builder.append(" = ");
        serialiseExpression(node.expression(), builder);
        builder.append(";");
        builder.newLine();
    }
}
