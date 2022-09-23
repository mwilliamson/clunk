package org.zwobble.clunk.typechecker;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedConstructedTypeNode;
import org.zwobble.clunk.ast.typed.TypedTypeLevelExpressionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.ListTypeConstructor;
import org.zwobble.clunk.types.TypeConstructorTypeSet;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelReferenceNode;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckConstructedTypeTests {
    @Test
    public void whenReceiverIsNotTypeConstructorThenErrorIsThrown() {
        var untypedNode = Untyped.constructedType(
            Untyped.typeLevelReference("Bool"),
            List.of(Untyped.typeLevelReference("Int"))
        );

        var result = assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckTypeLevelExpressionNode(
            untypedNode,
            TypeCheckerContext.stub()
        ));

        assertThat(result.getExpected(), equalTo(TypeConstructorTypeSet.INSTANCE));
        assertThat(result.getActual(), equalTo(Types.typeLevelValueType(Types.BOOL)));
    }

    @Test
    public void canConstructTypeWithOneArgument() {
        var untypedNode = Untyped.constructedType(
            Untyped.typeLevelReference("List"),
            List.of(Untyped.typeLevelReference("Int"))
        );
        var context = TypeCheckerContext.stub();

        var result = (TypedConstructedTypeNode) TypeChecker.typeCheckTypeLevelExpressionNode(untypedNode, context);

        assertThat(result.receiver(), isTypedTypeLevelReferenceNode("List", ListTypeConstructor.INSTANCE));
        assertThat(result.args(), contains(isArg(isTypedTypeLevelReferenceNode("Int", Types.INT))));
        assertThat(result.value(), equalTo(Types.list(Types.INT)));
    }

    private static Matcher<TypedConstructedTypeNode.Arg> isArg(Matcher<TypedTypeLevelExpressionNode> type) {
        return has("type", type);
    }
}
