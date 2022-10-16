package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedSwitchCaseNode;
import org.zwobble.clunk.ast.untyped.UntypedSwitchNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserSwitchTests {
    @Test
    public void canParseSwitchStatement() {
        var source = """
            switch (x) {
                case A {
                    return 1;
                }
                case B {
                    return 2;
                }
            }
            """;

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, cast(
            UntypedSwitchNode.class,
            has("expression", isUntypedReferenceNode("x")),
            has("cases", contains(
                cast(
                    UntypedSwitchCaseNode.class,
                    has("type", isUntypedTypeLevelReferenceNode("A")),
                    has("body", contains(
                        isUntypedReturnNode().withExpression(isUntypedIntLiteralNode(1))
                    ))
                ),
                cast(
                    UntypedSwitchCaseNode.class,
                    has("type", isUntypedTypeLevelReferenceNode("B")),
                    has("body", contains(
                        isUntypedReturnNode().withExpression(isUntypedIntLiteralNode(2))
                    ))
                )
            ))
        ));
    }

    @Test
    public void blankLinesAreAllowedBeforeAndAfterEachCase() {
        var source = """
            switch (x) {

                case A {
                    return 1;
                }

                case B {
                    return 2;
                }

            }
            """;

        var node = parseString(source, Parser::parseFunctionStatement);

        assertThat(node, cast(
            UntypedSwitchNode.class,
            has("cases", contains(
                cast(
                    UntypedSwitchCaseNode.class,
                    has("type", isUntypedTypeLevelReferenceNode("A"))
                ),
                cast(
                    UntypedSwitchCaseNode.class,
                    has("type", isUntypedTypeLevelReferenceNode("B"))
                )
            ))
        ));
    }
}
