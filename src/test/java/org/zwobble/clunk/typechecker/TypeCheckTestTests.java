package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedTestNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckTestTests {
    @Test
    public void bodyIsTypeChecked() {
        var untypedNode = UntypedTestNode.builder()
            .addBodyStatement(Untyped.var("x", Untyped.boolFalse()))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(result.typedNode(), isTypedTestNode().withBody(contains(
            isTypedVarNode().withExpression(isTypedBoolLiteralNode(false))
        )));
    }

    @Test
    public void returnedContextLeavesBodyEnvironment() {
        var untypedNode = UntypedTestNode.builder()
            .addBodyStatement(Untyped.var("x", Untyped.boolFalse()))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(result.context().currentFrame().environment().containsKey("x"), equalTo(false));
    }
}
