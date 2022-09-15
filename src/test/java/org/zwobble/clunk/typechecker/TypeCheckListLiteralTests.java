package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedListLiteralNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedIntLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckListLiteralTests {
    @Test
    public void emptyListHasObjectElementType() {
        var untypedNode = Untyped.listLiteral(List.of());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedListLiteralNode.class,
            has("elements", empty()),
            has("elementType", equalTo(Types.OBJECT))
        ));
    }

    @Test
    public void singletonListUsesTypeOfElement() {
        var untypedNode = Untyped.listLiteral(List.of(
            Untyped.intLiteral(42)
        ));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedListLiteralNode.class,
            has("elements", contains(
                isTypedIntLiteralNode(42)
            )),
            has("elementType", equalTo(Types.INT))
        ));
    }

    @Test
    public void whenElementsAreTheSameTypeThenListUsesElementType() {
        var untypedNode = Untyped.listLiteral(List.of(
            Untyped.intLiteral(42),
            Untyped.intLiteral(47)
        ));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedListLiteralNode.class,
            has("elements", contains(
                isTypedIntLiteralNode(42),
                isTypedIntLiteralNode(47)
            )),
            has("elementType", equalTo(Types.INT))
        ));
    }
}
