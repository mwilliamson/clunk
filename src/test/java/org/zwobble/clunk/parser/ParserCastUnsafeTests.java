package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedCastUnsafeNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserCastUnsafeTests {
    @Test
    public void canParseCastUnsafe() {
        var source = "x as String";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, cast(
            UntypedCastUnsafeNode.class,
            has("expression", isUntypedReferenceNode("x")),
            has("typeExpression", isUntypedTypeLevelReferenceNode("String"))
        ));
    }
}
