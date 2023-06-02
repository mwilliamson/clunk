package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIntEqualsNode;
import org.zwobble.clunk.ast.typed.TypedStringEqualsNode;
import org.zwobble.clunk.ast.typed.TypedStructuredEqualsNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.StructuredTypeSet;
import org.zwobble.clunk.types.Types;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckEqualsTests {
    @Test
    public void whenOperandsAreIntsThenExpressionIsTypedAsIntEquals() {
        var untypedNode = Untyped.equals(Untyped.intLiteral(42), Untyped.intLiteral(47));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedIntEqualsNode.class,
            has("left", x -> x.left(), isTypedIntLiteralNode(42)),
            has("right", x -> x.right(), isTypedIntLiteralNode(47))
        ));
    }

    @Test
    public void givenLeftOperandIsIntWhenRightOperandIsNotIntThenErrorIsThrown() {
        var untypedNode = Untyped.equals(Untyped.intLiteral(), Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(Types.INT));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }

    @Test
    public void whenOperandsAreStringsThenExpressionIsTypedAsStringEquals() {
        var untypedNode = Untyped.equals(Untyped.string("a"), Untyped.string("b"));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedStringEqualsNode.class,
            has("left", x -> x.left(), isTypedStringLiteralNode("a")),
            has("right", x -> x.right(), isTypedStringLiteralNode("b"))
        ));
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

    @Test
    public void whenOperandsAreStructuredTypesThenExpressionIsTypedAsStructuredEquals() {
        var untypedNode = Untyped.equals(Untyped.reference("a"), Untyped.reference("b"));
        var context = TypeCheckerContext.stub()
            .addLocal("a", Types.map(Types.STRING, Types.STRING), NullSource.INSTANCE)
            .addLocal("b", Types.map(Types.STRING, Types.STRING), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedStructuredEqualsNode.class,
            has("left", x -> x.left(), isTypedReferenceNode().withName("a")),
            has("right", x -> x.right(), isTypedReferenceNode().withName("b"))
        ));
    }

    @Test
    public void givenLeftOperandIsStructuredTypeWhenRightOperandIsNotStructuredTypeThenErrorIsThrown() {
        var untypedNode = Untyped.equals(Untyped.reference("a"), Untyped.boolFalse());
        var context = TypeCheckerContext.stub()
            .addLocal("a", Types.map(Types.STRING, Types.STRING), NullSource.INSTANCE);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(StructuredTypeSet.INSTANCE));
        assertThat(result.getActual(), equalTo(Types.BOOL));
    }
}
