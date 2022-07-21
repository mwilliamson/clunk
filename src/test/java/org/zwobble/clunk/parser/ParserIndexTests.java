package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserIndexTests {
    @Test
    public void canParseIndex() {
        var source = "values[42]";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedIndexNode()
            .withReceiver(isUntypedReferenceNode("values"))
            .withIndex(isUntypedIntLiteralNode(42))
        );
    }
}
