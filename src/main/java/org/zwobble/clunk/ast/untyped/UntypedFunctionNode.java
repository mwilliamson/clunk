package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record UntypedFunctionNode(
    String name,
    List<UntypedArgNode> args,
    UntypedStaticExpressionNode returnType,
    Source source
) implements UntypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static UntypedFunctionNode.Builder builder() {
        return new UntypedFunctionNode.Builder("f", List.of(), Untyped.staticReference("Int"), NullSource.INSTANCE);
    }

    public static record Builder(
        String name,
        List<UntypedArgNode> args,
        UntypedStaticExpressionNode returnType,
        Source source
    ) {
        public UntypedFunctionNode build() {
            return new UntypedFunctionNode(name, args, returnType, source);
        }

        public UntypedFunctionNode.Builder addArg(UntypedArgNode arg) {
            var args = new ArrayList<>(this.args);
            args.add(arg);
            return new UntypedFunctionNode.Builder(name, args, returnType, source);
        }

        public UntypedFunctionNode.Builder name(String name) {
            return new UntypedFunctionNode.Builder(name, args, returnType, source);
        }

        public UntypedFunctionNode.Builder returnType(UntypedStaticExpressionNode returnType) {
            return new UntypedFunctionNode.Builder(name, args, returnType, source);
        }
    }

}
