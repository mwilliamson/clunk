package org.zwobble.clunk.backends.java.ast;

public class JavaSerialiser {
    public static void serialiseOrdinaryCompilationUnit(JavaOrdinaryCompilationUnitNode node, StringBuilder builder) {
        serialisePackageDeclaration(node.packageDeclaration(), builder);
        builder.append("\n\n");
        serialiseRecordDeclaration(node.typeDeclaration(), builder);
    }

    private static void serialisePackageDeclaration(String packageDeclaration, StringBuilder builder) {
        builder.append("package ");
        builder.append(packageDeclaration);
        builder.append(";");
    }

    public static void serialiseRecordDeclaration(JavaRecordDeclarationNode node, StringBuilder builder) {
        builder.append("public record ");
        builder.append(node.name());
        builder.append("() {\n}");
    }
}
