package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedAddNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedIntLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.precisely.Matchers.instanceOf;

public class ParserAddTests {
    @Test
    public void canParseAddition() {
        var source = "1 + 2";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedAddNode.class,
            has("left", x -> x.left(), isUntypedIntLiteralNode(1)),
            has("right", x -> x.right(), isUntypedIntLiteralNode(2))
        ));
    }
}
