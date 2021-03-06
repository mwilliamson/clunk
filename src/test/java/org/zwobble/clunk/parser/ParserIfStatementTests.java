package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserIfStatementTests {
    @Test
    public void canParseIfStatementWithOneConditionalBranch() {
        var source = "if (true) { 42; 47; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(contains(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    contains(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42)),
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
                    )
                )
            ))
            .withElseBody(empty()));
    }

    @Test
    public void canParseIfStatementWithElseBranch() {
        var source = "if (true) { 42; } else { 47; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(contains(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    contains(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42))
                    )
                )
            ))
            .withElseBody(contains(
                isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
            )));
    }

    @Test
    public void canParseIfStatementWithMultipleConditionalBranches() {
        var source = "if (true) { 42; } else if (false) { 47; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(contains(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    contains(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42))
                    )
                ),
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(false),
                    contains(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
                    )
                )
            ))
            .withElseBody(empty()));
    }

    @Test
    public void canParseIfStatementWithMultipleConditionalBranchesAndElseBranch() {
        var source = "if (true) { 42; } else if (false) { 47; } else { 52; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(contains(
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(true),
                    contains(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42))
                    )
                ),
                isUntypedConditionalBranchNode(
                    isUntypedBoolLiteralNode(false),
                    contains(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
                    )
                )
            ))
            .withElseBody(contains(
                isUntypedExpressionStatementNode(isUntypedIntLiteralNode(52))
            )));
    }
}
