package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedIntLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;

public class ParserIntLiteralTests {
    @Test
    public void canParseZero() {
        var source = "0";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedIntLiteralNode(0));
    }

    @Test
    public void canParsePositiveInteger() {
        var source = "123";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedIntLiteralNode(123));
    }

    @Test
    public void canParseNegativeInteger() {
        var source = "-123";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedIntLiteralNode(-123));
    }
}
