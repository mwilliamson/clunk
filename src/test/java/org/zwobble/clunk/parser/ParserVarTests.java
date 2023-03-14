package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;

public class ParserVarTests {
    @Test
    public void canParseVar() {
        var source = "var x = false;";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedVarNode().withName("x").withExpression((isUntypedBoolLiteralNode(false))));
    }
}
