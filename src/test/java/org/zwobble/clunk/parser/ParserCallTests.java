package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedCall;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserCallTests {
    @Test
    public void canParseCallWithNoArguments() {
        var source = "f()";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedCall()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(empty())
        );
    }

    @Test
    public void canParseCallOfCall() {
        var source = "f()()";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedCall()
            .withReceiver(isUntypedCall()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(empty()))
            .withPositionalArgs(empty())
        );
    }
}
