package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.types.NamespaceName;

import java.util.Optional;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;

public class ParserNamespaceTests {
    @Test
    public void canParseImportsOfNamespaces() {
        var source = "import Example;\nimport Stdlib/Assert;\nimport Stdlib/Matchers;";

        var node = parseString(
            source,
            (parser, tokens) -> parser.parseNamespace(
                tokens,
                NamespaceName.fromParts("example", "project"),
                SourceType.SOURCE
            )
        );

        assertThat(node, isUntypedNamespaceNode().withImports(isSequence(
            isUntypedImportNode(NamespaceName.fromParts("Example"), Optional.empty()),
            isUntypedImportNode(NamespaceName.fromParts("Stdlib", "Assert"), Optional.empty()),
            isUntypedImportNode(NamespaceName.fromParts("Stdlib", "Matchers"), Optional.empty())
        )));
    }

    @Test
    public void canParseImportsOfNamespaceFields() {
        var source = "import a.B;\nimport a/b.C;\nimport a/b/c.D;";

        var node = parseString(
            source,
            (parser, tokens) -> parser.parseNamespace(
                tokens,
                NamespaceName.fromParts("example", "project"),
                SourceType.SOURCE
            )
        );

        assertThat(node, isUntypedNamespaceNode().withImports(isSequence(
            isUntypedImportNode(NamespaceName.fromParts("a"), Optional.of("B")),
            isUntypedImportNode(NamespaceName.fromParts("a", "b"), Optional.of("C")),
            isUntypedImportNode(NamespaceName.fromParts("a", "b", "c"), Optional.of("D"))
        )));
    }

    @Test
    public void canParseStatements() {
        var source = "record First(name: String)\nrecord Second(name: String)";

        var node = parseString(
            source,
            (parser, tokens) -> parser.parseNamespace(
                tokens,
                NamespaceName.fromParts("example", "project"),
                SourceType.SOURCE
            )
        );

        assertThat(node, isUntypedNamespaceNode().withStatements(isSequence(
            isUntypedRecordNode().withName("First"),
            isUntypedRecordNode().withName("Second")
        )));
    }

    @Test
    public void canParseBlankLines() {
        var source = "record First(name: String)\n\nrecord Second(name: String)";

        var node = parseString(
            source,
            (parser, tokens) -> parser.parseNamespace(
                tokens,
                NamespaceName.fromParts("example", "project"),
                SourceType.SOURCE
            )
        );

        assertThat(node, isUntypedNamespaceNode().withStatements(isSequence(
            isUntypedRecordNode().withName("First"),
            isUntypedBlankLineNode(),
            isUntypedRecordNode().withName("Second")
        )));
    }
}
