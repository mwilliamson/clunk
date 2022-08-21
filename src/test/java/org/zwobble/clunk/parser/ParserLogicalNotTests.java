package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedLogicalNotNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserLogicalNotTests {
    @Test
    public void canParseLogicalNot() {
        var source = "!a";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, cast(
            UntypedLogicalNotNode.class,
            has("operand", isUntypedReferenceNode("a"))
        ));
    }
}
