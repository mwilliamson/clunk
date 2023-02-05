package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

public record TypedCallMethodNode(
    Optional<TypedExpressionNode> receiver,
    String methodName,
    TypedArgsNode args,
    Type type,
    Source source
) implements TypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(
            Optional.empty(),
            "f",
            List.of(),
            Types.OBJECT
        );
    }

    public record Builder(
        Optional<TypedExpressionNode> receiver,
        String methodName,
        List<TypedExpressionNode> positionalArgs,
        Type type
    ) {
        public Builder receiver(TypedExpressionNode newReceiver) {
            return new Builder(Optional.of(newReceiver), methodName, positionalArgs, type);
        }

        public Builder methodName(String newMethodName) {
            return new Builder(receiver, newMethodName, positionalArgs, type);
        }

        public Builder positionalArgs(List<TypedExpressionNode> newPositionalArgs) {
            return new Builder(receiver, methodName, newPositionalArgs, type);
        }

        public Builder type(Type newType) {
            return new Builder(receiver, methodName, positionalArgs, newType);
        }

        public TypedCallMethodNode build() {
            return new TypedCallMethodNode(
                receiver,
                methodName,
                new TypedArgsNode(positionalArgs, NullSource.INSTANCE),
                type,
                NullSource.INSTANCE
            );
        }
    }
}
