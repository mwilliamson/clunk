package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedPropertyNode(
    String name,
    UntypedTypeLevelExpressionNode type,
    List<UntypedFunctionStatementNode> body,
    Source source
) implements UntypedRecordBodyDeclarationNode {
}
