package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;

public class ParserExpressionStatementTests {
    @Test
    public void canParseExpressionStatement() {
        var source = "false;";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedExpressionStatementNode(isUntypedBoolLiteralNode(false)));
    }
}
