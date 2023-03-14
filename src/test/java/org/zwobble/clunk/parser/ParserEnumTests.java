package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedEnumNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.isSequence;

public class ParserEnumTests {
    @Test
    public void canParseEmptyEnum() {
        var source = "enum NoteType { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedEnumNode(equalTo("NoteType"), isSequence()));
    }

    @Test
    public void canParseEnumWithOneMember() {
        var source = "enum NoteType { FOOTNOTE }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedEnumNode(equalTo("NoteType"), isSequence(equalTo("FOOTNOTE"))));
    }

    @Test
    public void canParseEnumWithMultipleMembers() {
        var source = "enum NoteType { FOOTNOTE, ENDNOTE }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedEnumNode(equalTo("NoteType"), isSequence(equalTo("FOOTNOTE"), equalTo("ENDNOTE"))));
    }

    @Test
    public void membersCanHaveTrailingComma() {
        var source = "enum NoteType { FOOTNOTE, }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedEnumNode(equalTo("NoteType"), isSequence(equalTo("FOOTNOTE"))));
    }
}
