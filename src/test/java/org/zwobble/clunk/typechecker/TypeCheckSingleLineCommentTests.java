package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedBoolLiteralNode;
import org.zwobble.clunk.ast.typed.TypedExpressionStatementNode;
import org.zwobble.clunk.ast.typed.TypedSingleLineCommentNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckSingleLineCommentTests {
    @Test
    public void canTypeCheckSingleLineCommentInFunction() {
        var untypedNode = Untyped.singleLineComment(" Beware.");

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.value(), cast(
            TypedSingleLineCommentNode.class,
            has("value", equalTo(" Beware."))
        ));
    }

    @Test
    public void canTypeCheckSingleLineCommentInNamespace() {
        var untypedNode = Untyped.singleLineComment(" Beware.");

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), cast(
            TypedSingleLineCommentNode.class,
            has("value", equalTo(" Beware."))
        ));
    }
}
