package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedRecordNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedStaticReferenceNode;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class ParserNamespaceTests {
    @Test
    public void canParseNamespace() {
        var source = "record First(name: String)\nrecord Second(name: String)";

        var tokens = Tokeniser.tokenise(source);
        var node = parser().parseNamespace(tokens, List.of("example", "project"));

        assertThat(node, has("statements", contains(
            isUntypedRecordNode(has("name", equalTo("First"))),
            isUntypedRecordNode(has("name", equalTo("Second")))
        )));
    }

    private Parser parser() {
        return new Parser("<filename>", "<contents>");
    }
}
