package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedAddNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedIntLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserAddTests {
    @Test
    public void canParseAddition() {
        var source = "1 + 2";

        var result = parseString(source, Parser::parseExpression);

        assertThat(result, cast(
            UntypedAddNode.class,
            has("left", isUntypedIntLiteralNode(1)),
            has("right", isUntypedIntLiteralNode(2))
        ));
    }
}
