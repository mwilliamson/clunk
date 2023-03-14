package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedSwitchCaseNode;
import org.zwobble.clunk.ast.untyped.UntypedSwitchNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

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

        assertThat(node, instanceOf(
            UntypedSwitchNode.class,
            has("expression", x -> x.expression(), isUntypedReferenceNode("x")),
            has("cases", x -> x.cases(), isSequence(
                instanceOf(
                    UntypedSwitchCaseNode.class,
                    has("type", x -> x.type(), isUntypedTypeLevelReferenceNode("A")),
                    has("body", x -> x.body(), isSequence(
                        isUntypedReturnNode().withExpression(isUntypedIntLiteralNode(1))
                    ))
                ),
                instanceOf(
                    UntypedSwitchCaseNode.class,
                    has("type", x -> x.type(), isUntypedTypeLevelReferenceNode("B")),
                    has("body", x -> x.body(), isSequence(
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

        assertThat(node, instanceOf(
            UntypedSwitchNode.class,
            has("cases", x -> x.cases(), isSequence(
                instanceOf(
                    UntypedSwitchCaseNode.class,
                    has("type", x -> x.type(), isUntypedTypeLevelReferenceNode("A"))
                ),
                instanceOf(
                    UntypedSwitchCaseNode.class,
                    has("type", x -> x.type(), isUntypedTypeLevelReferenceNode("B"))
                )
            ))
        ));
    }
}
