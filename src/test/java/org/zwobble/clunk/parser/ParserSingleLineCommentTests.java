package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedSingleLineCommentNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedBoolLiteralNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedExpressionStatementNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserSingleLineCommentTests {
    @Test
    public void canParseSingleLineCommentAsFunctionStatement() {
        var source = "// Beware.";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, cast(UntypedSingleLineCommentNode.class, has("value", equalTo(" Beware."))));
    }

    @Test
    public void canParseSingleLineCommentAsNamespaceStatement() {
        var source = "// Beware.";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, cast(UntypedSingleLineCommentNode.class, has("value", equalTo(" Beware."))));
    }
}
