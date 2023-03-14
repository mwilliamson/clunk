package org.zwobble.clunk.typechecker;

import org.zwobble.precisely.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedConstructedTypeNode;
import org.zwobble.clunk.ast.typed.TypedTypeLevelExpressionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.TypeConstructorTypeSet;
import org.zwobble.clunk.types.Types;
import org.zwobble.clunk.types.Variance;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelReferenceNode;
import static org.zwobble.precisely.Matchers.has;

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

        assertThat(result.receiver(), isTypedTypeLevelReferenceNode("List", Types.LIST_CONSTRUCTOR));
        assertThat(result.args(), isSequence(
            isArg(isTypedTypeLevelReferenceNode("Int", Types.INT))
        ));
        assertThat(result.value(), equalTo(Types.list(Types.INT)));
    }

    // TODO: make these non-specific to List/Option/MutableList

    @Test
    public void listTypeArgIsCovariant() {
        var untypedNode = Untyped.constructedType(
            Untyped.typeLevelReference("List"),
            List.of(Untyped.typeLevelReference("Int"))
        );
        var context = TypeCheckerContext.stub();

        var result = (TypedConstructedTypeNode) TypeChecker.typeCheckTypeLevelExpressionNode(untypedNode, context);

        assertThat(result.args(), isSequence(
            isArg(isTypedTypeLevelReferenceNode("Int", Types.INT), Variance.COVARIANT)
        ));
    }

    @Test
    public void optionTypeArgIsCovariant() {
        var untypedNode = Untyped.constructedType(
            Untyped.typeLevelReference("Option"),
            List.of(Untyped.typeLevelReference("Int"))
        );
        var context = TypeCheckerContext.stub();

        var result = (TypedConstructedTypeNode) TypeChecker.typeCheckTypeLevelExpressionNode(untypedNode, context);

        assertThat(result.args(), isSequence(
            isArg(isTypedTypeLevelReferenceNode("Int", Types.INT), Variance.COVARIANT)
        ));
    }

    @Test
    public void mutableListTypeArgIsInvariant() {
        var untypedNode = Untyped.constructedType(
            Untyped.typeLevelReference("MutableList"),
            List.of(Untyped.typeLevelReference("Int"))
        );
        var context = TypeCheckerContext.stub();

        var result = (TypedConstructedTypeNode) TypeChecker.typeCheckTypeLevelExpressionNode(untypedNode, context);

        assertThat(result.args(), isSequence(
            isArg(isTypedTypeLevelReferenceNode("Int", Types.INT), Variance.INVARIANT)
        ));
    }

    private static Matcher<TypedConstructedTypeNode.Arg> isArg(Matcher<TypedTypeLevelExpressionNode> type, Variance variance) {
        return allOf(
            has("type", x -> x.type(), type),
            has("variance", x -> x.variance(), equalTo(variance))
        );
    }

    private static Matcher<TypedConstructedTypeNode.Arg> isArg(Matcher<TypedTypeLevelExpressionNode> type) {
        return allOf(has("type", x -> x.type(), type));
    }
}
