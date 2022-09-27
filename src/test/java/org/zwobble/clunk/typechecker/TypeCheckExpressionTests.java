package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedStaticMethodToFunctionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckExpressionTests {
    @Test
    public void whenExpectedTypeIsFunctionAndActualTypeIsStaticFunctionThenStaticFunctionIsConverted() {
        var untypedNode = Untyped.reference("f");
        var methodType = new StaticFunctionType(
            NamespaceName.fromParts(),
            "f",
            List.of(Types.STRING),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("f", methodType, NullSource.INSTANCE);
        var expectedType = Types.functionType(
            List.of(Types.STRING),
            Types.INT
        );

        var result = TypeChecker.typeCheckExpression(untypedNode, expectedType, context);

        assertThat(result, cast(
            TypedStaticMethodToFunctionNode.class,
            has("method", isTypedReferenceNode().withName("f").withType(methodType)),
            has("type", equalTo(Types.functionType(
                List.of(Types.STRING),
                Types.INT
            )))
        ));
    }
}
