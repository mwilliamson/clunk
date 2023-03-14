package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedLogicalOrNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class ParserLogicalOrTests {
    @Test
    public void canParseLogicalOr() {
        var source = "a || b";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedLogicalOrNode.class,
            has("left", x -> x.left(), isUntypedReferenceNode("a")),
            has("right", x -> x.right(), isUntypedReferenceNode("b"))
        ));
    }
}
