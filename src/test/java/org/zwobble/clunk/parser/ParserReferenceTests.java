package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;

public class ParserReferenceTests {
    @Test
    public void canParseReference() {
        var source = "oneTwoThree";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedReferenceNode("oneTwoThree"));
    }
}
