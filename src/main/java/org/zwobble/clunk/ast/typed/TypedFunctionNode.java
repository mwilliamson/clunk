package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.IntType;

import java.util.ArrayList;
import java.util.List;

public record TypedFunctionNode(
    String name,
    List<TypedArgNode> args,
    TypedStaticExpressionNode returnType,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


    public static Builder builder() {
        return new Builder("f", List.of(), Typed.staticExpression(IntType.INSTANCE));
    }

    public static record Builder(
        String name,
        List<TypedArgNode> args,
        TypedStaticExpressionNode returnType
    ) {
        public TypedFunctionNode build() {
            return new TypedFunctionNode(name, args, returnType, NullSource.INSTANCE);
        }

        public Builder addArg(TypedArgNode arg) {
            var args = new ArrayList<>(this.args);
            args.add(arg);
            return new Builder(name, args, returnType);
        }

        public Builder name(String name) {
            return new Builder(name, args, returnType);
        }
    }
}
