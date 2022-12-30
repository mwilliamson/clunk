package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIntNotEqualNode;
import org.zwobble.clunk.ast.typed.TypedStringNotEqualNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedIntLiteralNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedStringLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckNotEqualTests {
    @Test
    public void whenOperandsAreIntsThenExpressionIsTypedAsIntNotEqual() {
        var untypedNode = Untyped.notEqual(Untyped.intLiteral(42), Untyped.intLiteral(47));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedIntNotEqualNode.class,
            has("left", isTypedIntLiteralNode(42)),
            has("right", isTypedIntLiteralNode(47))
        ));
    }

    @Test
    public void whenOperandsAreStringsThenExpressionIsTypedAsStringNotEqual() {
        var untypedNode = Untyped.notEqual(Untyped.string("a"), Untyped.string("b"));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedStringNotEqualNode.class,
            has("left", isTypedStringLiteralNode("a")),
            has("right", isTypedStringLiteralNode("b"))
        ));
    }

    @Test
    public void whenLeftOperandIsNotStringThenErrorIsThrown() {
        var untypedNode = Untyped.notEqual(Untyped.boolFalse(), Untyped.string());
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
        var untypedNode = Untyped.notEqual(Untyped.string(), Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.STRING));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }
}
