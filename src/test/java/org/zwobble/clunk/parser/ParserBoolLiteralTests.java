package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedBoolLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;

public class ParserBoolLiteralTests {
    @Test
    public void canParseFalse() {
        var source = "false";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedBoolLiteralNode(false));
    }

    @Test
    public void canParseTrue() {
        var source = "true";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedBoolLiteralNode(true));
    }
}
