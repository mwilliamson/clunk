package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserIfStatementTests {
    @Test
    public void canParseIfStatementWithOnlyTrueBranch() {
        var source = "if (true) { 42; 47; }";

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, isUntypedIfStatementNode()
            .withConditionalBranches(contains(
                isUntypedConditionalBranch(
                    isUntypedBoolLiteralNode(true),
                    contains(
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(42)),
                        isUntypedExpressionStatementNode(isUntypedIntLiteralNode(47))
                    )
                )
            ))
            .withElseBranch(empty()));
    }
}
