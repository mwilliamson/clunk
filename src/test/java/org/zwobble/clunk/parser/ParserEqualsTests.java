package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedEqualsNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedStringLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class ParserEqualsTests {
    @Test
    public void canParseEquality() {
        var source = "\"a\" == \"b\"";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedEqualsNode.class,
            has("left", x -> x.left(), isUntypedStringLiteralNode("a")),
            has("right", x -> x.right(), isUntypedStringLiteralNode("b"))
        ));
    }
}
