package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserRecordTests {
    @Test
    public void canParseRecordName() {
        var source = "record User(name: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode(untypedRecordNodeHasName("User")));
    }

    @Test
    public void canParseSingleField() {
        var source = "record User(name: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode(untypedRecordNodeHasFields(contains(
            allOf(has("name", equalTo("name")), has("type", isUntypedStaticReferenceNode("String")))
        ))));
    }

    @Test
    public void canParseMultipleFields() {
        var source = "record User(name: String, emailAddress: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode(untypedRecordNodeHasFields(contains(
            has("name", equalTo("name")),
            has("name", equalTo("emailAddress"))
        ))));
    }

    @Test
    public void fieldsCanHaveTrailingComma() {
        var source = "record User(name: String, emailAddress: String,)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode(untypedRecordNodeHasFields(contains(
            has("name", equalTo("name")),
            has("name", equalTo("emailAddress"))
        ))));
    }
}
