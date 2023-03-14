package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;

public class ParserIfStatementTests {
    @Test
    public void canParseIfStatementWithOneConditionalBranch() {
        var source = "if (true) { 42; 47; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(isSequence(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    isSequence(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42)),
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
                    )
                )
            ))
            .withElseBody(isSequence()));
    }

    @Test
    public void canParseIfStatementWithElseBranch() {
        var source = "if (true) { 42; } else { 47; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(isSequence(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    isSequence(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42))
                    )
                )
            ))
            .withElseBody(isSequence(
                isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
            )));
    }

    @Test
    public void canParseIfStatementWithMultipleConditionalBranches() {
        var source = "if (true) { 42; } else if (false) { 47; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(isSequence(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    isSequence(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42))
                    )
                ),
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(false),
                    isSequence(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
                    )
                )
            ))
            .withElseBody(isSequence()));
    }

    @Test
    public void canParseIfStatementWithMultipleConditionalBranchesAndElseBranch() {
        var source = "if (true) { 42; } else if (false) { 47; } else { 52; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(isSequence(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    isSequence(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42))
                    )
                ),
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(false),
                    isSequence(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
                    )
                )
            ))
            .withElseBody(isSequence(
                isUntypedExpressionStatementNode(isUntypedIntLiteralNode(52))
            )));
    }
}
