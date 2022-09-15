package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedListLiteralNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedIntLiteralNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserListLiteralTests {
    @Test
    public void canParseEmptyList() {
        var source = "[]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, cast(
            UntypedListLiteralNode.class,
            has("elements", empty())
        ));
    }

    @Test
    public void canParseSingletonList() {
        var source = "[42]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, cast(
            UntypedListLiteralNode.class,
            has("elements", contains(isUntypedIntLiteralNode(42)))
        ));
    }

    @Test
    public void canParseListWithMultipleElements() {
        var source = "[42, 47, 52]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, cast(
            UntypedListLiteralNode.class,
            has("elements", contains(
                isUntypedIntLiteralNode(42),
                isUntypedIntLiteralNode(47),
                isUntypedIntLiteralNode(52)
            ))
        ));
    }
}
