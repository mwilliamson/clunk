package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserMapLiteralTests {
    @Test
    public void canParseEmptyMap() {
        var source = "#[]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedMapLiteralNode(empty()));
    }

    @Test
    public void canParseSingletonMap() {
        var source = "#[[1, 2]]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedMapLiteralNode(contains(
            isUntypedMapEntryLiteralNode(isUntypedIntLiteralNode(1), isUntypedIntLiteralNode(2))
        )));
    }

    @Test
    public void canParseListWithMultipleElements() {
        var source = "#[[1, 2], [3, 4], [5, 6]]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedMapLiteralNode(contains(
            isUntypedMapEntryLiteralNode(isUntypedIntLiteralNode(1), isUntypedIntLiteralNode(2)),
            isUntypedMapEntryLiteralNode(isUntypedIntLiteralNode(3), isUntypedIntLiteralNode(4)),
            isUntypedMapEntryLiteralNode(isUntypedIntLiteralNode(5), isUntypedIntLiteralNode(6))
        )));
    }

    @Test
    public void mapLiteralCanHaveTrailingComma() {
        var source = "#[[1, 2],]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedMapLiteralNode(contains(
            isUntypedMapEntryLiteralNode(isUntypedIntLiteralNode(1), isUntypedIntLiteralNode(2))
        )));
    }
}
