package org.zwobble.clunk.ast;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record RecordNode(String name, List<RecordFieldNode> fields, Source source) implements NamespaceStatementNode {
}
