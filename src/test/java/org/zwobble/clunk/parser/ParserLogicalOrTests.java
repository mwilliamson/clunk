package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedLogicalOrNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserLogicalOrTests {
    @Test
    public void canParseLogicalOr() {
        var source = "a || b";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, cast(
            UntypedLogicalOrNode.class,
            has("left", isUntypedReferenceNode("a")),
            has("right", isUntypedReferenceNode("b"))
        ));
    }
}
