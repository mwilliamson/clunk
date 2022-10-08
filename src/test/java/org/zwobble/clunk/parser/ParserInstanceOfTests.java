package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedInstanceOfNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserInstanceOfTests {
    @Test
    public void canParseInstanceOf() {
        var source = "x instanceof Y";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, cast(
            UntypedInstanceOfNode.class,
            has("expression", isUntypedReferenceNode("x")),
            has("type", isUntypedTypeLevelReferenceNode("Y"))
        ));
    }
}
