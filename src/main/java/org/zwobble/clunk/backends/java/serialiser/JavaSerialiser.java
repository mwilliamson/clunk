package org.zwobble.clunk.backends.java.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.*;

import java.util.List;
import java.util.Optional;

import static org.zwobble.clunk.util.Iterables.forEachInterspersed;

public class JavaSerialiser {
    private static void serialiseAdd(JavaAddNode node, CodeBuilder builder) {
        serialiseBinaryOperation("+", node, builder);
    }

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
                serialiseExpression(node.value(), builder, Optional.empty());
                builder.append(")");
                return null;
            }
        });
    }
    private static void serialiseBinaryOperation(String operator, JavaBinaryOperationNode node, CodeBuilder builder) {
        serialiseExpression(node.left(), builder, Optional.of(node));
        builder.append(" ");
        builder.append(operator);
        builder.append(" ");
        serialiseExpression(node.right(), builder, Optional.of(node));
    }

    private static void serialiseBlankLine(JavaBlankLineNode node, CodeBuilder builder) {
        builder.newLine();
    }

    private static void serialiseBlock(List<JavaStatementNode> statements, CodeBuilder builder) {
        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var statement : statements) {
            serialiseStatement(statement, builder);
        }
        builder.dedent();
        builder.append("}");
    }

    private static void serialiseBoolLiteral(JavaBoolLiteralNode node, CodeBuilder builder) {
        builder.append(node.value() ? "true" : "false");
    }

    private static void serialiseCall(JavaCallNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        builder.append("(");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder, Optional.empty()),
            () -> builder.append(", ")
        );
        builder.append(")");
    }

    private static void serialiseCallNew(JavaCallNewNode node, CodeBuilder builder) {
        builder.append("new ");
        serialiseExpression(node.receiver(), builder, Optional.of(node));

        if (node.typeArgs().isPresent()) {
            builder.append("<");
            forEachInterspersed(
                node.typeArgs().get(),
                typeArg -> serialiseTypeExpression(typeArg, builder),
                () -> builder.append(", ")
            );
            builder.append(">");
        }

        builder.append("(");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder, Optional.empty()),
            () -> builder.append(", ")
        );
        builder.append(")");

        if (node.body().isPresent()) {
            builder.append(" {");
            builder.newLine();
            builder.indent();
            for (var bodyDeclaration : node.body().get()) {
                serialiseClassBodyDeclaration(bodyDeclaration, builder);
            }
            builder.dedent();
            builder.append("}");
        }
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
            public Void visit(JavaBlankLineNode node) {
                serialiseBlankLine(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaMethodDeclarationNode node) {
                serialiseMethodDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaSingleLineCommentNode node) {
                serialiseSingleLineComment(node, builder);
                return null;
            }
        });
    }

    private static void serialiseEnumDeclaration(JavaEnumDeclarationNode node, CodeBuilder builder) {
        builder.append("public enum ");
        builder.append(node.name());
        builder.append(" {");
        builder.indent();
        forEachInterspersed(
            node.members(),
            member -> {
                builder.newLine();
                builder.append(member);
            },
            () -> builder.append(",")
        );
        builder.dedent();
        builder.newLine();
        builder.append("}");
    }

    public static void serialiseExpression(JavaExpressionNode node, CodeBuilder builder, Optional<JavaExpressionNode> parent) {
        var parenthesize = parent.isPresent() && node.precedence().ordinal() < parent.get().precedence().ordinal();
        if (parenthesize) {
            builder.append("(");
        }

        node.accept(new JavaExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(JavaAddNode node) {
                serialiseAdd(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaBoolLiteralNode node) {
                serialiseBoolLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaCallNode node) {
                serialiseCall(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaCallNewNode node) {
                serialiseCallNew(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaIntLiteralNode node) {
                serialiseIntLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaLogicalAndNode node) {
                serialiseLogicalAnd(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaLogicalNotNode node) {
                serialiseLogicalNot(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaLogicalOrNode node) {
                serialiseLogicalOr(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaMemberAccessNode node) {
                serialiseMemberAccess(node, builder);
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

        if (parenthesize) {
            builder.append(")");
        }
    }

    private static void serialiseExpressionStatement(
        JavaExpressionStatementNode node,
        CodeBuilder builder
    ) {
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseFullyQualifiedTypeReference(
        JavaFullyQualifiedTypeReferenceNode node,
        CodeBuilder builder
    ) {
        builder.append(node.packageName());
        builder.append(".");
        builder.append(node.typeName());
    }

    private static void serialiseIfStatement(JavaIfStatementNode node, CodeBuilder builder) {
        builder.append("if (");
        var firstConditionalBranch = node.conditionalBranches().get(0);
        serialiseExpression(firstConditionalBranch.condition(), builder, Optional.empty());
        builder.append(")");
        serialiseBlock(firstConditionalBranch.body(), builder);

        node.conditionalBranches().stream().skip(1).forEachOrdered(conditionalBranch -> {
            builder.append(" else if (");
            serialiseExpression(conditionalBranch.condition(), builder, Optional.empty());
            builder.append(")");
            serialiseBlock(conditionalBranch.body(), builder);
        });

        if (node.elseBody().size() > 0) {
            builder.append(" else");
            serialiseBlock(node.elseBody(), builder);
        }

        builder.newLine();
    }

    public static void serialiseImport(JavaImportNode node, CodeBuilder builder) {
        node.accept(new JavaImportNode.Visitor<Void>() {
            @Override
            public Void visit(JavaImportStaticNode node) {
                serialiseImportStatic(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaImportTypeNode node) {
                serialiseImportType(node, builder);
                return null;
            }
        });
    }

    private static void serialiseImportStatic(JavaImportStaticNode node, CodeBuilder builder) {
        builder.append("import static ");
        builder.append(node.typeName());
        builder.append(".");
        builder.append(node.identifier());
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseImportType(JavaImportTypeNode node, CodeBuilder builder) {
        builder.append("import ");
        builder.append(node.typeName());
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseInterfaceDeclaration(JavaInterfaceDeclarationNode node, CodeBuilder builder) {
        builder.append("public ");
        if (node.permits().isPresent()) {
            builder.append("sealed ");
        }
        builder.append("interface ");
        builder.append(node.name());

        if (!node.typeParams().isEmpty()) {
            builder.append("<");
            forEachInterspersed(
                node.typeParams(),
                typeParam -> builder.append(typeParam),
                () -> builder.append(", ")
            );
            builder.append(">");
        }

        if (node.permits().isPresent()) {
            builder.append(" permits ");
            forEachInterspersed(
                node.permits().get(),
                permit -> serialiseTypeExpression(permit, builder),
                () -> builder.append(", ")
            );
        }

        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var memberDeclaration : node.body()) {
            serialiseInterfaceMemberDeclaration(memberDeclaration, builder);
        }
        builder.dedent();
        builder.append("}");
        builder.newLine();
    }

    public static void serialiseInterfaceMemberDeclaration(
        JavaInterfaceMemberDeclarationNode node,
        CodeBuilder builder
    ) {
        node.accept(new JavaInterfaceMemberDeclarationNode.Visitor<Void>() {
            @Override
            public Void visit(JavaInterfaceDeclarationNode node) {
                serialiseInterfaceDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaInterfaceMethodDeclarationNode node) {
                serialiseInterfaceMethodDeclaration(node, builder);
                return null;
            }
        });
    }

    private static void serialiseInterfaceMethodDeclaration(
        JavaInterfaceMethodDeclarationNode node,
        CodeBuilder builder
    ) {
        if (!node.typeParams().isEmpty()) {
            builder.append("<");
            forEachInterspersed(
                node.typeParams(),
                typeParam -> builder.append(typeParam),
                () -> builder.append(", ")
            );
            builder.append("> ");
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
        builder.append(");");
        builder.newLine();
    }

    private static void serialiseIntLiteral(JavaIntLiteralNode node, CodeBuilder builder) {
        builder.append(Integer.toString(node.value()));
    }

    private static void serialiseLogicalAnd(JavaLogicalAndNode node, CodeBuilder builder) {
        serialiseBinaryOperation("&&", node, builder);
    }

    private static void serialiseLogicalNot(JavaLogicalNotNode node, CodeBuilder builder) {
        builder.append("!");
        serialiseExpression(node.operand(), builder, Optional.of(node));
    }

    private static void serialiseLogicalOr(JavaLogicalOrNode node, CodeBuilder builder) {
        serialiseBinaryOperation("||", node, builder);
    }

    private static void serialiseMemberAccess(JavaMemberAccessNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        builder.append(".");
        builder.append(node.memberName());
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

        if (!node.typeParams().isEmpty()) {
            builder.append("<");
            forEachInterspersed(
                node.typeParams(),
                typeParam -> builder.append(typeParam),
                () -> builder.append(", ")
            );
            builder.append("> ");
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
        builder.append(")");
        serialiseBlock(node.body(), builder);
        builder.newLine();
    }

    public static void serialiseOrdinaryCompilationUnit(JavaOrdinaryCompilationUnitNode node, CodeBuilder builder) {
        serialisePackageDeclaration(node.packageDeclaration(), builder);
        builder.newLine();
        builder.newLine();
        if (!node.imports().isEmpty()) {
            for (var importNode : node.imports()) {
                serialiseImport(importNode, builder);
            }
            builder.newLine();
        }
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

    private static void serialiseParameterizedType(JavaParameterizedType node, CodeBuilder builder) {
        serialiseTypeExpression(node.receiver(), builder);
        builder.append("<");
        forEachInterspersed(
            node.args(),
            arg -> serialiseTypeExpression(arg, builder),
            () -> builder.append(", ")
        );
        builder.append(">");
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

        builder.append(") ");

        if (!node.implements_().isEmpty()) {
            builder.append("implements ");
            forEachInterspersed(
                node.implements_(),
                implementsType -> serialiseTypeExpression(implementsType, builder),
                () -> builder.append(", ")
            );
            builder.append(" ");
        }
        builder.append("{");
        builder.newLine();
        builder.indent();

        for (var bodyDeclaration : node.body()) {
            serialiseClassBodyDeclaration(bodyDeclaration, builder);
        }

        builder.dedent();
        builder.append("}");
    }

    private static void serialiseReference(JavaReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    private static void serialiseReturn(JavaReturnNode node, CodeBuilder builder) {
        builder.append("return ");
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseSingleLineComment(JavaSingleLineCommentNode node, CodeBuilder builder) {
        builder.append("//");
        builder.append(node.value());
        builder.newLine();
    }

    public static void serialiseStatement(JavaStatementNode node, CodeBuilder builder) {
        node.accept(new JavaStatementNode.Visitor<Void>() {
            @Override
            public Void visit(JavaBlankLineNode node) {
                serialiseBlankLine(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaExpressionStatementNode node) {
                serialiseExpressionStatement(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaIfStatementNode node) {
                serialiseIfStatement(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaReturnNode node) {
                serialiseReturn(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaSingleLineCommentNode node) {
                serialiseSingleLineComment(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaVariableDeclarationNode node) {
                serialiseVariableDeclaration(node, builder);
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
            public Void visit(JavaEnumDeclarationNode node) {
                serialiseEnumDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(JavaInterfaceDeclarationNode node) {
                serialiseInterfaceDeclaration(node, builder);
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
            public Void visit(JavaParameterizedType node) {
                serialiseParameterizedType(node, builder);
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
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(";");
        builder.newLine();
    }
}
