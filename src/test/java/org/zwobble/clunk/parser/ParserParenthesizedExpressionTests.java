package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedBoolLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserParenthesizedExpressionTests {
    @Test
    public void canParseParenthesizedExpression() {
        var source = "(false)";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedBoolLiteralNode(false));
    }
}
