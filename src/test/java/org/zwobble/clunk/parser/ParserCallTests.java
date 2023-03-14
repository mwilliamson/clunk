package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;

public class ParserCallTests {
    @Test
    public void canParseCallWithNoArguments() {
        var source = "f()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(isSequence())
        );
    }

    @Test
    public void canParseCallWithOnePositionalArgument() {
        var source = "f(true)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(isSequence(isUntypedBoolLiteralNode(true)))
        );
    }

    @Test
    public void canParseCallWithManyPositionalArguments() {
        var source = "f(true, false)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(isSequence(isUntypedBoolLiteralNode(true), isUntypedBoolLiteralNode(false)))
        );
    }

    @Test
    public void canParseCallWithOneNamedArgument() {
        var source = "f(.x = true)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(isSequence())
            .withNamedArgs(isSequence(isUntypedNamedArg("x", isUntypedBoolLiteralNode(true))))
        );
    }

    @Test
    public void canParseCallWithManyNamedArguments() {
        var source = "f(.x = true, .y = false)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(isSequence())
            .withNamedArgs(isSequence(
                isUntypedNamedArg("x", isUntypedBoolLiteralNode(true)),
                isUntypedNamedArg("y", isUntypedBoolLiteralNode(false))
            ))
        );
    }

    @Test
    public void canParseNamedArgumentAfterPositionalArgument() {
        var source = "f(true, .y = false)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(isSequence(
                isUntypedBoolLiteralNode(true)
            ))
            .withNamedArgs(isSequence(
                isUntypedNamedArg("y", isUntypedBoolLiteralNode(false))
            ))
        );
    }

    @Test
    public void cannotParsePositionalArgumentAfterNamedArgument() {
        var source = "f(.y = false, true)";

        assertThrows(
            PositionalArgAfterNamedArgError.class,
            () -> parseString(source, Parser::parseTopLevelExpression)
        );
    }

    @Test
    public void argumentsMayHaveTrailingCommand() {
        var source = "f(true,)";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedReferenceNode("f"))
            .withPositionalArgs(isSequence(isUntypedBoolLiteralNode(true)))
        );
    }

    @Test
    public void canParseCallOfCall() {
        var source = "f()()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withReceiver(isUntypedCallNode()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(isSequence()))
            .withPositionalArgs(isSequence())
        );
    }

    @Test
    public void canParseCallWithNoTypeLevelArguments() {
        var source = "f()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withTypeLevelArgs(isSequence())
        );
    }

    @Test
    public void canParseCallWithOneTypeLevelArgument() {
        var source = "f[A]()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withTypeLevelArgs(isSequence(isUntypedTypeLevelReferenceNode("A")))
        );
    }

    @Test
    public void canParseCallWithMultipleTypeLevelArguments() {
        var source = "f[A, B, C]()";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedCallNode()
            .withTypeLevelArgs(isSequence(
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
            .withTypeLevelArgs(isSequence(
                isUntypedTypeLevelReferenceNode("A")
            ))
        );
    }
}
