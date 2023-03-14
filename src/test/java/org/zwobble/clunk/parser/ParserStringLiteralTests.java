package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedStringLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.clunk.sources.SourceMatchers.isFileFragmentSource;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserStringLiteralTests {
    @Test
    public void canParseEmptyString() {
        var source = "\"\"";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedStringLiteralNode(""));
    }

    @Test
    public void canParseStringWithUnescapedCharacters() {
        var source = "\"abc 123 XYZ\"";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedStringLiteralNode("abc 123 XYZ"));
    }

    @Test
    public void canParseStringWithEscapedCharacters() {
        var source = "\"\\n\\r\\t\\\\\\\"\"";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedStringLiteralNode("\n\r\t\\\""));
    }

    @Test
    public void whenStringHasUnrecognisedEscapeSequenceThenExceptionIsThrown() {
        var source = "\"a\\ab\"";

        var error = assertThrows(
            UnrecognisedEscapeSequenceError.class,
            () -> parseString(source, Parser::parseTopLevelExpression)
        );

        assertThat(error.getMessage(), equalTo("Unrecognised escape sequence: \\a"));
        assertThat(error.getSource(), isFileFragmentSource(allOf(
            has("characterIndexStart", x -> x.characterIndexStart(), equalTo(1)),
            has("characterIndexEnd", x -> x.characterIndexEnd(), equalTo(3))
        )));
    }
}
