package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserRecordTests {
    @Test
    public void canParseRecordName() {
        var source = "record User(name: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withName("User"));
    }

    @Test
    public void canParseSingleField() {
        var source = "record User(name: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withFields(contains(
            allOf(
                isUntypedRecordFieldNode().withName("name"),
                isUntypedRecordFieldNode().withType(isUntypedStaticReferenceNode("String"))
            )
        )));
    }

    @Test
    public void canParseMultipleFields() {
        var source = "record User(name: String, emailAddress: String)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withFields(contains(
            isUntypedRecordFieldNode().withName("name"),
            isUntypedRecordFieldNode().withName("emailAddress")
        )));
    }

    @Test
    public void fieldsCanHaveTrailingComma() {
        var source = "record User(name: String, emailAddress: String,)";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withFields(contains(
            isUntypedRecordFieldNode().withName("name"),
            isUntypedRecordFieldNode().withName("emailAddress")
        )));
    }

    @Test
    public void whenSupertypesAreNotSpecifiedThenSupertypesIsEmpty() {
        var source = "record User()";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(empty()));
    }

    @Test
    public void canParseSingleSupertype() {
        var source = "record User() <: Person";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedRecordNode().withSupertypes(contains(isUntypedStaticReferenceNode("Person"))));
    }
}
