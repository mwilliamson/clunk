package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedForEachNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserForEachTests {
    @Test
    public void canParseForEach() {
        var source = """
            for (var x in xs) {
                print(x);
            }""";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, cast(
            UntypedForEachNode.class,
            has("targetName", equalTo("x")),
            has("iterable", isUntypedReferenceNode("xs")),
            has("body", contains(
                isUntypedExpressionStatementNode(
                    isUntypedCallNode()
                        .withReceiver(isUntypedReferenceNode("print"))
                        .withPositionalArgs(contains(isUntypedReferenceNode("x")))
                )
            ))
        ));
    }
}
