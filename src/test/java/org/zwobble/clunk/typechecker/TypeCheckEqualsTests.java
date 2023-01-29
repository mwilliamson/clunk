package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIntEqualsNode;
import org.zwobble.clunk.ast.typed.TypedStringEqualsNode;
import org.zwobble.clunk.ast.typed.TypedStructuredEqualsNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckEqualsTests {
    @Test
    public void whenOperandsAreIntsThenExpressionIsTypedAsIntEquals() {
        var untypedNode = Untyped.equals(Untyped.intLiteral(42), Untyped.intLiteral(47));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedIntEqualsNode.class,
            has("left", isTypedIntLiteralNode(42)),
            has("right", isTypedIntLiteralNode(47))
        ));
    }

    @Test
    public void whenOperandsAreStringsThenExpressionIsTypedAsStringEquals() {
        var untypedNode = Untyped.equals(Untyped.string("a"), Untyped.string("b"));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedStringEqualsNode.class,
            has("left", isTypedStringLiteralNode("a")),
            has("right", isTypedStringLiteralNode("b"))
        ));
    }

    @Test
    public void whenOperandsAreStringMapsThenExpressionIsTypedAsStructuredEquals() {
        var untypedNode = Untyped.equals(Untyped.reference("a"), Untyped.reference("b"));
        var context = TypeCheckerContext.stub()
            .addLocal("a", Types.map(Types.STRING, Types.STRING), NullSource.INSTANCE)
            .addLocal("b", Types.map(Types.STRING, Types.STRING), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedStructuredEqualsNode.class,
            has("left", isTypedReferenceNode().withName("a")),
            has("right", isTypedReferenceNode().withName("b"))
        ));
    }

    @Test
    public void whenLeftOperandIsNotStringThenErrorIsThrown() {
        var untypedNode = Untyped.equals(Untyped.boolFalse(), Untyped.string());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.STRING));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }

    @Test
    public void givenLeftOperandIsStringWhenRightOperandIsNotStringThenErrorIsThrown() {
        var untypedNode = Untyped.equals(Untyped.string(), Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.STRING));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }
}
