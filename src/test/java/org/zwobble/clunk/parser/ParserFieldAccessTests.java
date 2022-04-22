package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedFieldAccessNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserFieldAccessTests {
    @Test
    public void canParseFieldAccess() {
        var source = "x.f";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedFieldAccessNode(
            isUntypedReferenceNode("x"),
            equalTo("f")
        ));
    }
}
