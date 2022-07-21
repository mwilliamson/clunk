package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckIndexTests {
    @Test
    public void canIndexIntoLists() {
        var untypedNode = Untyped.index(
            Untyped.reference("values"),
            Untyped.intLiteral(123)
        );
        var context = TypeCheckerContext.stub()
            .addLocal("values", Types.list(Types.STRING), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedIndexNode()
            .withReceiver(isTypedReferenceNode().withName("values").withType(Types.list(Types.STRING)))
            .withIndex(isTypedIntLiteralNode(123))
            .withType(Types.STRING)
        );
    }

    @Test
    public void whenReceiverIsNotIndexableThenErrorIsThrown() {
        var untypedNode = Untyped.index(
            Untyped.reference("values"),
            Untyped.intLiteral(123)
        );
        var context = TypeCheckerContext.stub()
            .addLocal("values", Types.INT, NullSource.INSTANCE);

        var error = assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(IndexableTypeSet.INSTANCE));
        assertThat(error.getActual(), equalTo(Types.INT));
    }

    @Test
    public void whenReceiverIsListAndIndexIsNotIntThenErrorIsThrown() {
        var untypedNode = Untyped.index(
            Untyped.reference("values"),
            Untyped.string("123")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("values", Types.list(Types.STRING), NullSource.INSTANCE);

        var error = assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(Types.INT));
        assertThat(error.getActual(), equalTo(Types.STRING));
    }
}
