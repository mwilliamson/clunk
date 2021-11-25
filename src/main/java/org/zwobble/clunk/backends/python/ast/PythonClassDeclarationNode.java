package org.zwobble.clunk.backends.python.ast;

import java.util.ArrayList;
import java.util.List;

public record PythonClassDeclarationNode(
    String name,
    List<PythonExpressionNode> decorators,
    List<PythonStatementNode> statements
) implements PythonNode {
    public static Builder builder(String name) {
        return new Builder(name, List.of(), List.of());
    }

    public static record Builder(
        String name,
        List<PythonExpressionNode> decorators,
        List<PythonStatementNode> statements
    ) {
        public PythonClassDeclarationNode build() {
            return new PythonClassDeclarationNode(name, decorators, statements);
        }

        public Builder addDecorator(PythonExpressionNode expression) {
            var decorators = new ArrayList<>(this.decorators);
            decorators.add(expression);
            return new Builder(name, decorators, statements);
        }

        public Builder addStatement(PythonStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new Builder(name, decorators, statements);
        }
    }
}
