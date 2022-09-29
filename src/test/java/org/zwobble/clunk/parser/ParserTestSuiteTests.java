package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserTestSuiteTests {
    @Test
    public void canParseEmptyTestSuite() {
        var source = "testSuite \"suite\" { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedTestSuiteNode()
            .withName("suite")
            .withTests(empty())
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

        assertThat(node, isUntypedTestSuiteNode().withTests(contains(
            isUntypedTestNode().withName("testOne"),
            isUntypedTestNode().withName("testTwo")
        )));
    }
}
