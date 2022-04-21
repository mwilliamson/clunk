package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserExpressionStatementTests {
    @Test
    public void canParseExpressionStatement() {
        var source = "false;";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedExpressionStatementNode(isUntypedBoolLiteralNode(false)));
    }
}
