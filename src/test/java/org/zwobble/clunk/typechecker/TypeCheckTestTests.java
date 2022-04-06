package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedTestNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckTestTests {
    @Test
    public void bodyIsTypeChecked() {
        var untypedNode = UntypedTestNode.builder()
            .addBodyStatement(Untyped.var("x", Untyped.boolFalse()))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(untypedNode);

        assertThat(result, isTypedTestNode().withBody(contains(
            isTypedVarNode().withExpression(isTypedBoolLiteralNode(false))
        )));
    }
}
