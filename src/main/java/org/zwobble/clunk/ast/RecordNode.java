package org.zwobble.clunk.ast;

import java.util.List;

public record RecordNode(String name, List<RecordFieldNode> fields) implements NamespaceStatementNode {
}
