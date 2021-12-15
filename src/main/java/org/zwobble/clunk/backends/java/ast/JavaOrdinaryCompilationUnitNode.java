package org.zwobble.clunk.backends.java.ast;

public record JavaOrdinaryCompilationUnitNode(
    String packageDeclaration,
    JavaTypeDeclarationNode typeDeclaration
) {
}
