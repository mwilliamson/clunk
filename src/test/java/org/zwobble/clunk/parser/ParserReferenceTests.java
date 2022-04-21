package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserReferenceTests {
    @Test
    public void canParseReference() {
        var source = "oneTwoThree";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedReferenceNode("oneTwoThree"));
    }
}
