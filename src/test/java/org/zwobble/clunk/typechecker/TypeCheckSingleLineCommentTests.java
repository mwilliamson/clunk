package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedSingleLineCommentNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckSingleLineCommentTests {
    @Test
    public void canTypeCheckSingleLineCommentInFunction() {
        var untypedNode = Untyped.singleLineComment(" Beware.");

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedSingleLineCommentNode.class,
                has("value", x -> x.value(), equalTo(" Beware."))
            )
        ));
    }

    @Test
    public void canTypeCheckSingleLineCommentInNamespace() {
        var untypedNode = Untyped.singleLineComment(" Beware.");

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), instanceOf(
            TypedSingleLineCommentNode.class,
            has("value", x -> x.value(), equalTo(" Beware."))
        ));
    }
}
