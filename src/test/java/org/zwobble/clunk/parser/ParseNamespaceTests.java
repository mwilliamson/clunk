package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.untyped.NodeMatchers.isRecordNode;
import static org.zwobble.clunk.ast.untyped.NodeMatchers.isStaticReferenceNode;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class ParseNamespaceTests {
    @Test
    public void canParseNamespace() {
        var source = "record First(name: String)\nrecord Second(name: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespace(tokens, List.of("example", "project"));

        System.out.println(node.statements());

        assertThat(node, has("statements", contains(
            isRecordNode(has("name", equalTo("First"))),
            isRecordNode(has("name", equalTo("Second")))
        )));
    }

    @Test
    public void canParseSingleField() {
        var source = "record User(name: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespaceStatement(tokens);

        assertThat(node, isRecordNode(has("fields", contains(
            allOf(has("name", equalTo("name")), has("type", isStaticReferenceNode("String")))
        ))));
    }

    @Test
    public void canParseMultipleFields() {
        var source = "record User(name: String, emailAddress: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespaceStatement(tokens);

        assertThat(node, isRecordNode(has("fields", contains(
            has("name", equalTo("name")),
            has("name", equalTo("emailAddress"))
        ))));
    }

    @Test
    public void fieldsCanHaveTrailingComma() {
        var source = "record User(name: String, emailAddress: String,)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespaceStatement(tokens);

        assertThat(node, isRecordNode(has("fields", contains(
            has("name", equalTo("name")),
            has("name", equalTo("emailAddress"))
        ))));
    }

    private Parser parser() {
        return new Parser("<filename>", "<contents>");
    }
}
