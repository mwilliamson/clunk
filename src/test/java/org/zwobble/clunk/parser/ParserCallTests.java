package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserCallTests {
    @Test
    public void canParseCallWithNoArguments() {
        var source = "f()";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(empty())
        );
    }

    @Test
    public void canParseCallWithOneArgument() {
        var source = "f(true)";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(contains(isUntypedBoolLiteralNode(true)))
        );
    }

    @Test
    public void canParseCallWithManyArguments() {
        var source = "f(true, false)";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(contains(isUntypedBoolLiteralNode(true), isUntypedBoolLiteralNode(false)))
        );
    }

    @Test
    public void argumentsMayHaveTrailingCommand() {
        var source = "f(true,)";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(contains(isUntypedBoolLiteralNode(true)))
        );
    }

    @Test
    public void canParseCallOfCall() {
        var source = "f()()";

        var node = parseString(source, Parser::parseExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedCallNode()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(empty()))
            .withPositionalArgs(empty())
        );
    }
}
