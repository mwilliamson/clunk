package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.IntType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;

public class TypeCheckReferenceTests {
    @Test
    public void referenceHasTypeOfNameInEnvironment() {
        var untypedNode = Untyped.reference("x");
        var context = TypeCheckerContext.stub()
            .addLocal("x", IntType.INSTANCE, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedReferenceNode().withName("x").withType(IntType.INSTANCE));
    }
}
