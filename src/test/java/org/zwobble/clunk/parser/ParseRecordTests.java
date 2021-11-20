package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedRecordNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedStaticReferenceNode;

public class ParseRecordTests {
    @Test
    public void canParseRecordName() {
        var source = "record User(name: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespaceStatement(tokens);

        assertThat(node, isUntypedRecordNode(has("name", equalTo("User"))));
    }

    @Test
    public void canParseSingleField() {
        var source = "record User(name: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespaceStatement(tokens);

        assertThat(node, isUntypedRecordNode(has("fields", contains(
            allOf(has("name", equalTo("name")), has("type", isUntypedStaticReferenceNode("String")))
        ))));
    }

    @Test
    public void canParseMultipleFields() {
        var source = "record User(name: String, emailAddress: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespaceStatement(tokens);

        assertThat(node, isUntypedRecordNode(has("fields", contains(
            has("name", equalTo("name")),
            has("name", equalTo("emailAddress"))
        ))));
    }

    @Test
    public void fieldsCanHaveTrailingComma() {
        var source = "record User(name: String, emailAddress: String,)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespaceStatement(tokens);

        assertThat(node, isUntypedRecordNode(has("fields", contains(
            has("name", equalTo("name")),
            has("name", equalTo("emailAddress"))
        ))));
    }

    private Parser parser() {
        return new Parser("<filename>", "<contents>");
    }
}
