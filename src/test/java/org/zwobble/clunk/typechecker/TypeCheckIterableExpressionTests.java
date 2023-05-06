package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeCheckIterableExpressionTests {
    @Test
    public void whenExpressionIsIterableThenTypedExpressionAndElementTypeAreReturned() {
        var untypedNode = Untyped.reference("xs");
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.STRING), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckIterableExpression(untypedNode, context);

        assertThat(result.iterable(), isTypedReferenceNode().withName("xs").withType(Types.list(Types.STRING)));
        assertThat(result.elementType(), equalTo(Types.STRING));
    }

    @Test
    public void whenExpressionIsNotListThenErrorIsThrown() {
        var untypedNode = Untyped.reference("xs");
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.INT, NullSource.INSTANCE);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckIterableExpression(untypedNode, context)
        );

        assertThat(result.getActual(), equalTo(Types.INT));
        assertThat(result.getExpected(), equalTo(Types.list(Types.OBJECT)));
    }
}
