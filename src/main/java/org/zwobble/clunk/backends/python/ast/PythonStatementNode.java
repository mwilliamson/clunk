package org.zwobble.clunk.backends.python.ast;

public interface PythonStatementNode extends PythonNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(PythonAssertNode node);
        T visit(PythonAssignmentNode node);
        T visit(PythonBlankLineNode node);
        T visit(PythonClassDeclarationNode node);
        T visit(PythonExpressionStatementNode node);
        T visit(PythonFunctionNode node);
        T visit(PythonIfStatementNode node);
        T visit(PythonImportNode node);
        T visit(PythonImportFromNode node);
        T visit(PythonReturnNode node);
        T visit(PythonSingleLineCommentNode node);
    }
}
