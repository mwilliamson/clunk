package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.ast.typed.TypedTestSuiteNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorTestSuiteTests {
    @Test
    public void testSuiteIsCompiledToClass() {
        var node = TypedTestSuiteNode.builder()
            .name("test suite")
            .addBodyStatement(
                TypedTestNode.builder()
                    .name("test case")
                    .build()
            )
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                class TestSuiteTests:
                    def test_test_case(self):
                        pass
                """
        ));
    }
}
