package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedForEachNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserForEachTests {
    @Test
    public void canParseForEach() {
        var source = """
            for (var x in xs) {
                print(x);
            }""";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, instanceOf(
            UntypedForEachNode.class,
            has("targetName", x -> x.targetName(), equalTo("x")),
            has("iterable", x -> x.iterable(), isUntypedReferenceNode("xs")),
            has("body", x -> x.body(), isSequence(
                isUntypedExpressionStatementNode(
                    isUntypedCallNode()
                        .withReceiver(isUntypedReferenceNode("print"))
                        .withPositionalArgs(isSequence(isUntypedReferenceNode("x")))
                )
            ))
        ));
    }
}
