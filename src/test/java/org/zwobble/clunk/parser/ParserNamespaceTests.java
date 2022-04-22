package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserNamespaceTests {
    @Test
    public void canParseImports() {
        var source = "import Example;\nimport Stdlib.Assert;\nimport Stdlib.Matchers;";

        var node = parseString(source, (parser, tokens) -> parser.parseNamespace(tokens, List.of("example", "project")));

        assertThat(node, isUntypedNamespaceNode().withImports(contains(
            isUntypedImportNode(List.of("Example")),
            isUntypedImportNode(List.of("Stdlib", "Assert")),
            isUntypedImportNode(List.of("Stdlib", "Matchers"))
        )));
    }

    @Test
    public void canParseStatements() {
        var source = "record First(name: String)\nrecord Second(name: String)";

        var node = parseString(source, (parser, tokens) -> parser.parseNamespace(tokens, List.of("example", "project")));

        assertThat(node, isUntypedNamespaceNode().withStatements(contains(
            isUntypedRecordNode().withName("First"),
            isUntypedRecordNode().withName("Second")
        )));
    }
}
