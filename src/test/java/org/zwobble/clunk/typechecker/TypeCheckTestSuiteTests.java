package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.ast.untyped.UntypedTestSuiteNode;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedFunctionNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTestSuiteNode;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckTestSuiteTests {
    // TODO: if we allow definitions in suites (?), then we need to uniquify
    // them: at the moment, record types defined in different test suites in the
    // same test suite will be considered the same record type.
    @Test
    public void bodyIsTypeChecked() {
        var untypedNode = UntypedTestSuiteNode.builder()
            .addBodyStatement(UntypedFunctionNode.builder().name("f").build())
            .build();
        var context = TypeCheckerContext.stub();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.typedNode(), isTypedTestSuiteNode().withBody(isSequence(
            isTypedFunctionNode().withName("f")
        )));
    }

    @Test
    public void returnedContextLeavesBodyEnvironment() {
        var untypedNode = UntypedTestSuiteNode.builder()
            .addBodyStatement(UntypedFunctionNode.builder().name("f").build())
            .build();
        var context = TypeCheckerContext.stub();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.context().currentFrame().environment().containsKey("f"), equalTo(false));
    }
}
