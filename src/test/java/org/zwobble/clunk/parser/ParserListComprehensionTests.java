package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedComprehensionIterableNode;
import org.zwobble.clunk.ast.untyped.UntypedExpressionNode;
import org.zwobble.clunk.ast.untyped.UntypedListComprehensionNode;
import org.zwobble.precisely.Matcher;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserListComprehensionTests {
    @Test
    public void canParseComprehensionWithSingleIterableAndNoIfs() {
        var source = "[for x in xs yield x]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, instanceOf(
            UntypedListComprehensionNode.class,
            hasIterables(isSequence(
                allOf(
                    hasTargetName("x"),
                    hasIterable(isUntypedReferenceNode("xs"))
                )
            )),
            hasYield(isUntypedReferenceNode("x"))
        ));
    }

    @Test
    public void canParseComprehensionWithMultipleIterablesAndNoIfs() {
        var source = "[for x in xs for y in ys yield x]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, instanceOf(
            UntypedListComprehensionNode.class,
            hasIterables(isSequence(
                allOf(
                    hasTargetName("x"),
                    hasIterable(isUntypedReferenceNode("xs"))
                ),
                allOf(
                    hasTargetName("y"),
                    hasIterable(isUntypedReferenceNode("ys"))
                )
            )),
            hasYield(isUntypedReferenceNode("x"))
        ));
    }

    private Matcher<UntypedListComprehensionNode> hasIterables(
        Matcher<? super Iterable<UntypedComprehensionIterableNode>> iterables
    ) {
        return has("iterables", x -> x.iterables(), iterables);
    }

    private Matcher<UntypedListComprehensionNode> hasYield(
        Matcher<? super UntypedExpressionNode> yield
    ) {
        return has("yield", x -> x.yield(), yield);
    }

    private Matcher<UntypedComprehensionIterableNode> hasIterable(
        Matcher<? super UntypedExpressionNode> iterable
    ) {
        return has("iterable", x -> x.iterable(), iterable);
    }

    private Matcher<UntypedComprehensionIterableNode> hasTargetName(
        String targetName
    ) {
        return has("targetName", x -> x.targetName(), equalTo(targetName));
    }
}
