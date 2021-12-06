package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedBoolLiteralNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedBoolLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserBoolLiteralTests {
    @Test
    public void canParseFalse() {
        var source = "false";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedBoolLiteralNode(false));
    }

    @Test
    public void canParseTrue() {
        var source = "true";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedBoolLiteralNode(true));
    }
}
