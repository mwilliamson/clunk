package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedInterfaceNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserInterfaceTests {
    @Test
    public void canParseEmptySealedInterface() {
        var source = "sealed interface DocumentElement { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, cast(UntypedInterfaceNode.class, has("name", equalTo("DocumentElement"))));
    }
}
