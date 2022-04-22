package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckCallTests {
    @Test
    public void canTypeCheckCallToStaticFunction() {
        var untypedNode = Untyped.call(
            Untyped.fieldAccess(Untyped.reference("Math"), "abs"),
            List.of(Untyped.intLiteral(123))
        );
        var context = TypeCheckerContext.stub()
            .updateType("Math", new NamespaceType(
                List.of("Stdlib", "Math"),
                Map.of("abs", new FunctionType(List.of(Types.INT), Types.INT))
            ));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallNode()
            .withReceiver(isTypedReceiverStaticFunctionNode(List.of("Stdlib", "Math"), "abs"))
            .withPositionalArgs(contains(isTypedIntLiteralNode(123)))
        );
    }

    @Test
    public void whenArgIsWrongTypeThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.fieldAccess(Untyped.reference("Math"), "abs"),
            List.of(Untyped.string("123"))
        );
        var context = TypeCheckerContext.stub()
            .updateType("Math", new NamespaceType(
                List.of("Stdlib", "Math"),
                Map.of("abs", new FunctionType(List.of(Types.INT), Types.INT))
            ));

        var error = assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(IntType.INSTANCE));
        assertThat(error.getActual(), equalTo(StringType.INSTANCE));
    }
}
