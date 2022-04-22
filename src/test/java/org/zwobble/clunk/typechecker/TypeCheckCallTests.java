package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckCallTests {
    @Test
    public void canTypeCheckCallToStaticFunction() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.intLiteral(123))
        );
        var functionType = new StaticFunctionType(
            List.of("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .updateType("abs", functionType);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedCallNode()
            .withReceiver(isTypedReferenceNode().withName("abs").withType(functionType))
            .withPositionalArgs(contains(isTypedIntLiteralNode(123)))
            .withType(Types.INT)
        );
    }

    @Test
    public void whenPositionalArgIsWrongTypeThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.string("123"))
        );
        var functionType = new StaticFunctionType(
            List.of("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .updateType("abs", functionType);

        var error = assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(IntType.INSTANCE));
        assertThat(error.getActual(), equalTo(StringType.INSTANCE));
    }

    @Test
    public void whenPositionalArgIsMissingThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of()
        );
        var functionType = new StaticFunctionType(
            List.of("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .updateType("abs", functionType);

        var error = assertThrows(WrongNumberOfArgumentsError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(1));
        assertThat(error.getActual(), equalTo(0));
    }

    @Test
    public void whenExtraPositionalArgIsPassedThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.reference("abs"),
            List.of(Untyped.intLiteral(123), Untyped.intLiteral(456))
        );
        var functionType = new StaticFunctionType(
            List.of("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub()
            .updateType("abs", functionType);

        var error = assertThrows(WrongNumberOfArgumentsError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(1));
        assertThat(error.getActual(), equalTo(2));
    }
}
