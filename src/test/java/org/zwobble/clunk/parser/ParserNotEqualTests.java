package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedNotEqualNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedStringLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserNotEqualTests {
    @Test
    public void canParseInequality() {
        var source = "\"a\" != \"b\"";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, cast(
            UntypedNotEqualNode.class,
            has("left", isUntypedStringLiteralNode("a")),
            has("right", isUntypedStringLiteralNode("b"))
        ));
    }
}
