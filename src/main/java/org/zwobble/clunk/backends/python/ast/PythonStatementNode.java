package org.zwobble.clunk.backends.python.ast;

public interface PythonStatementNode extends PythonNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(PythonAssignmentNode node);
        T visit(PythonClassDeclarationNode node);
        T visit(PythonFunctionNode node);
        T visit(PythonImportNode node);
        T visit(PythonReturnNode node);
    }
}
