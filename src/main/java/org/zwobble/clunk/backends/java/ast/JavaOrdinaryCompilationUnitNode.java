package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaOrdinaryCompilationUnitNode(
    String packageDeclaration,
    List<JavaImportNode> imports,
    JavaTypeDeclarationNode typeDeclaration
) {
}
