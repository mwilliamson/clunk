package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelExpressionNode;

public class TypeCheckTypeLevelReferenceTests {
    @Test
    public void staticReferenceToBoolHasBoolType() {
        var untypedNode = Untyped.typeLevelReference("Bool");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, isTypedTypeLevelExpressionNode(BoolType.INSTANCE));
    }

    @Test
    public void staticReferenceToIntHasIntType() {
        var untypedNode = Untyped.typeLevelReference("Int");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, isTypedTypeLevelExpressionNode(IntType.INSTANCE));
    }

    @Test
    public void staticReferenceToStringHasStringType() {
        var untypedNode = Untyped.typeLevelReference("String");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, isTypedTypeLevelExpressionNode(StringType.INSTANCE));
    }
}
