package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.ast.typed.TypedTestSuiteNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorTestSuiteTests {
    @Test
    public void testIsCompiledToTestSuiteFunctionCall() {
        var node = TypedTestSuiteNode.builder()
            .name("test suite")
            .addBodyStatement(
                TypedTestNode.builder()
                    .name("test case")
                    .build()
            )
            .build();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            suite("test suite", function () {
                test("test case", function () {
                });
            });
            """
        ));
    }
}
