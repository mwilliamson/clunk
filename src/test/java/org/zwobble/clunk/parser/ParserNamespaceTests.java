package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserNamespaceTests {
    @Test
    public void canParseNamespace() {
        var source = "record First(name: String)\nrecord Second(name: String)";

        var node = parseString(source, (parser, tokens) -> parser.parseNamespace(tokens, List.of("example", "project")));

        assertThat(node, has("statements", contains(
            isUntypedRecordNode(untypedRecordNodeHasName("First")),
            isUntypedRecordNode(untypedRecordNodeHasName("Second"))
        )));
    }
}
