package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedInterfaceNode;

import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserInterfaceTests {
    @Test
    public void canParseEmptyUnsealedInterface() {
        var source = "interface HasChildren { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, instanceOf(
            UntypedInterfaceNode.class,
            has("name", x -> x.name(), equalTo("HasChildren")),
            has("isSealed", x -> x.isSealed(), equalTo(false))
        ));
    }

    @Test
    public void canParseEmptySealedInterface() {
        var source = "sealed interface DocumentElement { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, instanceOf(
            UntypedInterfaceNode.class,
            has("name", x -> x.name(), equalTo("DocumentElement")),
            has("isSealed", x -> x.isSealed(), equalTo(true))
        ));
    }
}
