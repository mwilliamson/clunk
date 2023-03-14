package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;

public class ParserTestSuiteTests {
    @Test
    public void canParseEmptyTestSuite() {
        var source = "testSuite \"suite\" { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedTestSuiteNode()
            .withName("suite")
            .withBody(isSequence())
        );
    }

    @Test
    public void canParseTestSuiteContainingTests() {
        var source = """
            testSuite "suite" {
                test "testOne" {
                }
                test "testTwo" {
                }
            }
            """;

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedTestSuiteNode().withBody(isSequence(
            isUntypedTestNode().withName("testOne"),
            isUntypedTestNode().withName("testTwo")
        )));
    }
}
