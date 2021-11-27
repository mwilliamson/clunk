package org.zwobble.clunk.backends.python.ast;

public record PythonKeywordArgumentNode(String name, PythonExpressionNode expression) implements PythonNode {
}
