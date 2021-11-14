package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.parser.HasRecordComponentWithValue.hasRecordComponent;
import static org.zwobble.clunk.parser.NodeMatchers.isRecordNode;
import static org.zwobble.clunk.parser.NodeMatchers.isStaticReferenceNode;

public class ParseRecordTests {
    @Test
    public void canParseRecordName() {
        var source = "record User(name: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = Parser.parseNamespaceStatement(tokens);

        assertThat(node, isRecordNode(hasRecordComponent("name", equalTo("User"))));
    }

    @Test
    public void canParseSingleField() {
        var source = "record User(name: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = Parser.parseNamespaceStatement(tokens);

        assertThat(node, isRecordNode(hasRecordComponent("fields", contains(
            allOf(hasRecordComponent("name", equalTo("name")), hasRecordComponent("type", isStaticReferenceNode("String")))
        ))));
    }

    @Test
    public void canParseMultipleFields() {
        var source = "record User(name: String, emailAddress: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = Parser.parseNamespaceStatement(tokens);

        assertThat(node, isRecordNode(hasRecordComponent("fields", contains(
            hasRecordComponent("name", equalTo("name")),
            hasRecordComponent("name", equalTo("emailAddress"))
        ))));
    }

    @Test
    public void fieldsCanHaveTrailingComma() {
        var source = "record User(name: String, emailAddress: String,)";

        var tokens = Tokeniser.tokenise(source);
        var node = Parser.parseNamespaceStatement(tokens);

        assertThat(node, isRecordNode(hasRecordComponent("fields", contains(
            hasRecordComponent("name", equalTo("name")),
            hasRecordComponent("name", equalTo("emailAddress"))
        ))));
    }
}
