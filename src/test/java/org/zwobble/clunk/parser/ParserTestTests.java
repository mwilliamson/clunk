package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserTestTests {
    @Test
    public void canParseEmptyTest() {
        var source = "test \"f\" { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedTestNode()
            .withName("f")
            .withBody(empty())
        );
    }

    @Test
    public void canParseFunctionWithBody() {
        var source = "test \"f\" { var x = true; var y = false; }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedTestNode().withBody(contains(
            isUntypedVarNode().withName("x"),
            isUntypedVarNode().withName("y")
        )));
    }
}
