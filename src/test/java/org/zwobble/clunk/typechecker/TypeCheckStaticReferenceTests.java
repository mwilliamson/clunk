package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckStaticReferenceTests {
    @Test
    public void staticReferenceToBoolHasBoolType() {
        var untypedNode = Untyped.staticReference("Bool");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, has("type", equalTo(BoolType.INSTANCE)));
    }

    @Test
    public void staticReferenceToIntHasIntType() {
        var untypedNode = Untyped.staticReference("Int");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, has("type", equalTo(IntType.INSTANCE)));
    }

    @Test
    public void staticReferenceToStringHasStringType() {
        var untypedNode = Untyped.staticReference("String");

        var typedNode = TypeChecker.typeCheckTypeLevelReferenceNode(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(typedNode, has("type", equalTo(StringType.INSTANCE)));
    }
}
