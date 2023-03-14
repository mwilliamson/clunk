package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedAddNode;
import org.zwobble.clunk.ast.untyped.UntypedLogicalAndNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserNestedExpressionTests {
    @Test
    public void precedenceOfBinaryOperatorsIsCorrectlyHandled() {
        var source = "1 + f()";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedAddNode.class,
            has("left", x -> x.left(), isUntypedIntLiteralNode(1)),
            has("right", x -> x.right(), isUntypedCallNode()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(isSequence())
            )
        ));
    }

    @Test
    public void precedenceOfUnaryOperatorsIsCorrectlyHandled() {
        var source = "!x && y";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedLogicalAndNode.class,
            has("left", x -> x.left(), isUntypedLogicalNotNode(isUntypedReferenceNode("x"))),
            has("right", x -> x.right(), isUntypedReferenceNode("y"))
        ));
    }

    @Test
    public void additionIsLeftAssociative() {
        var source = "1 + 2 + 3";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, instanceOf(
            UntypedAddNode.class,
            has("left", x -> x.left(), instanceOf(
                UntypedAddNode.class,
                has("left", x -> x.left(), isUntypedIntLiteralNode(1)),
                has("right", x -> x.right(), isUntypedIntLiteralNode(2))
            )),
            has("right", x -> x.right(), isUntypedIntLiteralNode(3))
        ));
    }

    @Test
    public void callsAreLeftAssociative() {
        var source = "f()()";

        var result = parseString(source, Parser::parseTopLevelExpression);

        assertThat(result, isUntypedCallNode()
            .withReceiver(isUntypedCallNode()
                .withReceiver(isUntypedReferenceNode("f"))
                .withPositionalArgs(isSequence())
            )
            .withPositionalArgs(isSequence())
        );
    }
}
