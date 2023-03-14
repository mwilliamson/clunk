package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.ast.typed.TypedTestSuiteNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorTestSuiteTests {
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

        var result = JavaCodeGenerator.compileTestSuite(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseClassBodyDeclaration);
        assertThat(string, equalTo(
            """
                @org.junit.jupiter.api.Nested
                public class TestSuite {
                    @org.junit.jupiter.api.Test
                    @org.junit.jupiter.api.DisplayName("test case")
                    public void testCase() {
                    }
                }"""
        ));
    }
}
