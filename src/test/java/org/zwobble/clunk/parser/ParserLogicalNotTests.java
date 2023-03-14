package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedLogicalNotNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class ParserLogicalNotTests {
    @Test
    public void canParseLogicalNot() {
        var source = "!a";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedLogicalNotNode.class,
            has("operand", x -> x.operand(), isUntypedReferenceNode("a"))
        ));
    }
}
