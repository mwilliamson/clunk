package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedAddNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserNestedExpressionTests {
    @Test
    public void precedenceIsCorrectlyHandled() {
        var source = "1 + f()";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, cast(
            UntypedAddNode.class,
            has("left", isUntypedIntLiteralNode(1)),
            has("right", isUntypedCallNode()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(empty())
            )
        ));
    }

    @Test
    public void additionIsLeftAssociative() {
        var source = "1 + 2 + 3";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, cast(
            UntypedAddNode.class,
            has("left", cast(
                UntypedAddNode.class,
                has("left", isUntypedIntLiteralNode(1)),
                has("right", isUntypedIntLiteralNode(2))
            )),
            has("right", isUntypedIntLiteralNode(3))
        ));
    }

    @Test
    public void callsAreLeftAssociative() {
        var source = "f()()";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, isUntypedCallNode()
            .withReceiver(isUntypedCallNode()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(empty())
            )
            .withPositionalArgs(empty())
        );
    }
}
