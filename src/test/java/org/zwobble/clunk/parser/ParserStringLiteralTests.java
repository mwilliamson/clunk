package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedStringLiteralNode;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.clunk.sources.SourceMatchers.isFileFragmentSource;

public class ParserStringLiteralTests {
    @Test
    public void canParseEmptyString() {
        var source = "\"\"";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedStringLiteralNode(""));
    }

    @Test
    public void canParseStringWithUnescapedCharacters() {
        var source = "\"abc 123 XYZ\"";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedStringLiteralNode("abc 123 XYZ"));
    }

    @Test
    public void canParseStringWithEscapedCharacters() {
        var source = "\"\\n\\r\\t\\\\\\\"\"";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedStringLiteralNode("\n\r\t\\\""));
    }

    @Test
    public void whenStringHasUnrecognisedEscapeSequenceThenExceptionIsThrown() {
        var source = "\"a\\ab\"";

        var error = assertThrows(
            UnrecognisedEscapeSequenceError.class,
            () -> parseString(source, Parser::parseExpression)
        );

        assertThat(error.getMessage(), equalTo("Unrecognised escape sequence: \\a"));
        assertThat(error.getSource(), isFileFragmentSource(allOf(
            has("characterIndexStart", equalTo(1)),
            has("characterIndexEnd", equalTo(3))
        )));
    }
}
