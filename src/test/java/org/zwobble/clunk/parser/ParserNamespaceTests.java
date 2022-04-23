package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.types.NamespaceName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserNamespaceTests {
    @Test
    public void canParseImports() {
        var source = "import Example;\nimport Stdlib/Assert;\nimport Stdlib/Matchers;";

        var node = parseString(
            source,
            (parser, tokens) -> parser.parseNamespace(
                tokens,
                NamespaceName.parts("example", "project")
            )
        );

        assertThat(node, isUntypedNamespaceNode().withImports(contains(
            isUntypedImportNode(NamespaceName.parts("Example")),
            isUntypedImportNode(NamespaceName.parts("Stdlib", "Assert")),
            isUntypedImportNode(NamespaceName.parts("Stdlib", "Matchers"))
        )));
    }

    @Test
    public void canParseStatements() {
        var source = "record First(name: String)\nrecord Second(name: String)";

        var node = parseString(
            source,
            (parser, tokens) -> parser.parseNamespace(
                tokens,
                NamespaceName.parts("example", "project")
            )
        );

        assertThat(node, isUntypedNamespaceNode().withStatements(contains(
            isUntypedRecordNode().withName("First"),
            isUntypedRecordNode().withName("Second")
        )));
    }
}
