package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Optional;

public record JavaCallNewNode(
    JavaExpressionNode receiver,
    Optional<? extends List<JavaTypeExpressionNode>> typeArgs,
    List<JavaExpressionNode> args,
    Optional<? extends List<? extends JavaClassBodyDeclarationNode>> body
) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.CALL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(JavaExpressionNode receiver) {
        return new Builder(receiver, Optional.empty(), P.vector(), Optional.empty());
    }

    public record Builder(
        JavaExpressionNode receiver,
        Optional<PVector<JavaTypeExpressionNode>> typeArgs,
        PVector<JavaExpressionNode> args,
        Optional<PVector<JavaClassBodyDeclarationNode>> body
    ) {
        public JavaCallNewNode build() {
            return new JavaCallNewNode(receiver, typeArgs, args, body);
        }

        public Builder inferTypeArgs() {
            return new Builder(receiver, Optional.of(P.vector()), args, body);
        }

        public Builder addTypeArg(JavaTypeExpressionNode typeArg) {
            return new Builder(
                receiver,
                Optional.of(typeArgs.orElse(P.vector()).plus(typeArg)),
                args,
                body
            );
        }

        public Builder addBodyDeclaration(JavaClassBodyDeclarationNode declaration) {
            return new Builder(
                receiver,
                typeArgs,
                args,
                Optional.of(body.orElse(P.vector()).plus(declaration))
            );
        }
    }
}
