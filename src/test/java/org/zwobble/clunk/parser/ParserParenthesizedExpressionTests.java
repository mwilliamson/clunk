package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedBoolLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;

public class ParserParenthesizedExpressionTests {
    @Test
    public void canParseParenthesizedExpression() {
        var source = "(false)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedBoolLiteralNode(false));
    }
}
