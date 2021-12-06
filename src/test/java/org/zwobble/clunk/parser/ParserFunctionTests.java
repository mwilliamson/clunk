package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.ast.untyped.UntypedStaticReferenceNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserFunctionTests {
    @Test
    public void canParseEmptyFunction() {
        var source = "fun f() -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasName("f"),
            untypedFunctionNodeHasReturnType(isUntypedStaticReferenceNode("String"))
        ));
    }
}
