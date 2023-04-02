package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedListLiteralNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedIntLiteralNode;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;

public class TypeCheckListLiteralTests {
    @Test
    public void givenNoTypeExpectationThenEmptyListHasNothingElementType() {
        var untypedNode = Untyped.listLiteral(List.of());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence()),
            has("elementType", x -> x.elementType(), equalTo(Types.NOTHING))
        ));
    }

    @Test
    public void givenNoTypeExpectationSingletonListUsesTypeOfElement() {
        var untypedNode = Untyped.listLiteral(List.of(
            Untyped.intLiteral(42)
        ));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence(
                isTypedIntLiteralNode(42)
            )),
            has("elementType", x -> x.elementType(), equalTo(Types.INT))
        ));
    }

    @Test
    public void givenNoTypeExpectationWhenElementsAreTheSameTypeThenListUsesElementType() {
        var untypedNode = Untyped.listLiteral(List.of(
            Untyped.intLiteral(42),
            Untyped.intLiteral(47)
        ));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence(
                isTypedIntLiteralNode(42),
                isTypedIntLiteralNode(47)
            )),
            has("elementType", x -> x.elementType(), equalTo(Types.INT))
        ));
    }

    @Test
    public void givenTypeExpectationThenEmptyListHasElementTypeMatchingExpectation() {
        var untypedNode = Untyped.listLiteral(List.of());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, Types.list(Types.STRING), context);

        assertThat(result, instanceOf(
            TypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence()),
            has("elementType", x -> x.elementType(), equalTo(Types.STRING))
        ));
    }

    @Test
    public void givenTypeExpectationWhenElementsAreMoreSpecificTypeThenListUsesExpectedType() {
        var untypedNode = Untyped.listLiteral(List.of(
            Untyped.intLiteral(42),
            Untyped.intLiteral(47)
        ));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, Types.list(Types.OBJECT), context);

        assertThat(result, instanceOf(
            TypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence(
                isTypedIntLiteralNode(42),
                isTypedIntLiteralNode(47)
            )),
            has("elementType", x -> x.elementType(), equalTo(Types.OBJECT))
        ));
    }

    @Test
    public void givenTypeExpectationWhenElementsAreWrongTypeThenErrorIsThrown() {
        var untypedNode = Untyped.listLiteral(List.of(
            Untyped.intLiteral(42),
            Untyped.intLiteral(47)
        ));
        var context = TypeCheckerContext.stub();

        var error = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, Types.list(Types.STRING), context)
        );

        assertThat(error.getExpected(), equalTo(Types.STRING));
        assertThat(error.getActual(), equalTo(Types.INT));
    }
}
