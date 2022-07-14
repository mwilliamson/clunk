package org.zwobble.clunk.backends.python.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record PythonClassDeclarationNode(
    String name,
    List<? extends PythonExpressionNode> decorators,
    List<? extends PythonExpressionNode> baseClasses,
    List<? extends PythonStatementNode> statements
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, P.vector(), P.vector(), P.vector());
    }

    public static record Builder(
        String name,
        PVector<PythonExpressionNode> decorators,
        PVector<PythonExpressionNode> baseClasses,
        PVector<PythonStatementNode> statements
    ) {
        public PythonClassDeclarationNode build() {
            return new PythonClassDeclarationNode(name, decorators, baseClasses, statements);
        }

        public Builder addDecorator(PythonExpressionNode expression) {
            return new Builder(name, decorators.plus(expression), baseClasses, statements);
        }

        public Builder addBaseClass(PythonExpressionNode baseClass) {
            return new Builder(name, decorators, baseClasses.plus(baseClass), statements);
        }


        public Builder addStatement(PythonStatementNode statement) {
            return new Builder(name, decorators, baseClasses, statements.plus(statement));
        }
    }
}
