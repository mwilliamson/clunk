package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIntAddNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedIntLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckAddTests {
    @Test
    public void whenOperandsAreIntsThenExpressionIsTypedAsIntAdd() {
        var untypedNode = Untyped.add(Untyped.intLiteral(1), Untyped.intLiteral(2));
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, cast(
            TypedIntAddNode.class,
            has("left", isTypedIntLiteralNode(1)),
            has("right", isTypedIntLiteralNode(2))
        ));
    }
}
