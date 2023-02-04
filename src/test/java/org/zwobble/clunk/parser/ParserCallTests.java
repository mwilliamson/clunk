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

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(empty())
        );
    }

    @Test
    public void canParseCallWithOnePositionalArgument() {
        var source = "f(true)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(contains(isUntypedBoolLiteralNode(true)))
        );
    }

    @Test
    public void canParseCallWithManyPositionalArguments() {
        var source = "f(true, false)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(contains(isUntypedBoolLiteralNode(true), isUntypedBoolLiteralNode(false)))
        );
    }

    @Test
    public void canParseCallWithOneNamedArgument() {
        var source = "f(.x = true)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(empty())
            .withNamedArgs(contains(isUntypedNamedArg("x", isUntypedBoolLiteralNode(true))))
        );
    }

    @Test
    public void argumentsMayHaveTrailingCommand() {
        var source = "f(true,)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(contains(isUntypedBoolLiteralNode(true)))
        );
    }

    @Test
    public void canParseCallOfCall() {
        var source = "f()()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedCallNode()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(empty()))
            .withPositionalArgs(empty())
        );
    }

    @Test
    public void canParseCallWithNoTypeLevelArguments() {
        var source = "f()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withTypeLevelArgs(empty())
        );
    }

    @Test
    public void canParseCallWithOneTypeLevelArgument() {
        var source = "f[A]()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withTypeLevelArgs(contains(isUntypedTypeLevelReferenceNode("A")))
        );
    }

    @Test
    public void canParseCallWithMultipleTypeLevelArguments() {
        var source = "f[A, B, C]()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withTypeLevelArgs(contains(
                isUntypedTypeLevelReferenceNode("A"),
                isUntypedTypeLevelReferenceNode("B"),
                isUntypedTypeLevelReferenceNode("C")
            ))
        );
    }

    @Test
    public void typeLevelArgumentsMayHaveTrailingCommand() {
        var source = "f[A,]()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withTypeLevelArgs(contains(
                isUntypedTypeLevelReferenceNode("A")
            ))
        );
    }
}
