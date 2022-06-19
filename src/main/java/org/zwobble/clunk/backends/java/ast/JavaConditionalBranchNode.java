package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaConditionalBranchNode(
    JavaExpressionNode condition,
    List<JavaStatementNode> body
) implements JavaNode {
}
