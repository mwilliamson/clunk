package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedStaticMethodToFunctionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;

public class TypeCheckExpressionTests {
    @Test
    public void whenExpectedTypeIsFunctionAndActualTypeIsStaticFunctionThenStaticFunctionIsConverted() {
        var untypedNode = Untyped.reference("f");
        var methodType = Types.staticFunctionType(
            NamespaceId.source(),
            "f",
            List.of(Types.STRING),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .addLocal("f", methodType, NullSource.INSTANCE);
        var expectedType = Types.functionType(
            List.of(Types.STRING),
            Types.OBJECT
        );

        var result = TypeChecker.typeCheckExpression(untypedNode, expectedType, context);

        assertThat(result, instanceOf(
            TypedStaticMethodToFunctionNode.class,
            has("method", x -> x.method(), isTypedReferenceNode().withName("f").withType(methodType)),
            has("type", x -> x.type(), equalTo(Types.functionType(
                List.of(Types.STRING),
                Types.INT
            )))
        ));
    }
}
