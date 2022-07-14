package org.zwobble.clunk.backends.python.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record PythonClassDeclarationNode(
    String name,
    List<? extends PythonExpressionNode> decorators,
    List<? extends PythonStatementNode> statements
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, P.vector(), P.vector());
    }

    public static record Builder(
        String name,
        PVector<PythonExpressionNode> decorators,
        PVector<PythonStatementNode> statements
    ) {
        public PythonClassDeclarationNode build() {
            return new PythonClassDeclarationNode(name, decorators, statements);
        }

        public Builder addDecorator(PythonExpressionNode expression) {
            return new Builder(name, decorators.plus(expression), statements);
        }

        public Builder addStatement(PythonStatementNode statement) {
            return new Builder(name, decorators, statements.plus(statement));
        }
    }
}
