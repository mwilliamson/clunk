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
            .withType(Types.INT)
        );
    }

    @Test
    public void whenNamespaceIsMissingFieldThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.fieldAccess(Untyped.reference("Math"), "noSuchFunction"),
            List.of(Untyped.intLiteral(123))
        );
        var namespaceType = new NamespaceType(
            List.of("Stdlib", "Math"),
            Map.of("abs", new FunctionType(List.of(Types.INT), Types.INT))
        );
        var context = TypeCheckerContext.stub()
            .updateType("Math", namespaceType);

        var error = assertThrows(UnknownFieldError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getType(), equalTo(namespaceType));
        assertThat(error.getFieldName(), equalTo("noSuchFunction"));
    }

    @Test
    public void whenPositionalArgIsWrongTypeThenErrorIsThrown() {
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

    @Test
    public void whenPositionalArgIsMissingThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.fieldAccess(Untyped.reference("Math"), "abs"),
            List.of()
        );
        var context = TypeCheckerContext.stub()
            .updateType("Math", new NamespaceType(
                List.of("Stdlib", "Math"),
                Map.of("abs", new FunctionType(List.of(Types.INT), Types.INT))
            ));

        var error = assertThrows(WrongNumberOfArgumentsError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(1));
        assertThat(error.getActual(), equalTo(0));
    }

    @Test
    public void whenExtraPositionalArgIsPassedThenErrorIsThrown() {
        var untypedNode = Untyped.call(
            Untyped.fieldAccess(Untyped.reference("Math"), "abs"),
            List.of(Untyped.intLiteral(123), Untyped.intLiteral(456))
        );
        var context = TypeCheckerContext.stub()
            .updateType("Math", new NamespaceType(
                List.of("Stdlib", "Math"),
                Map.of("abs", new FunctionType(List.of(Types.INT), Types.INT))
            ));

        var error = assertThrows(WrongNumberOfArgumentsError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(error.getExpected(), equalTo(1));
        assertThat(error.getActual(), equalTo(2));
    }
}
