package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;

public class ParserTestTests {
    @Test
    public void canParseEmptyTest() {
        var source = "test \"f\" { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedTestNode()
            .withName("f")
            .withBody(isSequence())
        );
    }

    @Test
    public void canParseFunctionWithBody() {
        var source = "test \"f\" { var x = true; var y = false; }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedTestNode().withBody(isSequence(
            isUntypedVarNode().withName("x"),
            isUntypedVarNode().withName("y")
        )));
    }
}
