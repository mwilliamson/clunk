package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserVarTests {
    @Test
    public void canParseVar() {
        var source = "var x = false;";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedVarNode().withName("x").withExpression((isUntypedBoolLiteralNode(false))));
    }
}
