package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedListLiteralNode;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedIntLiteralNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class ParserListLiteralTests {
    @Test
    public void canParseEmptyList() {
        var source = "[]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, instanceOf(
            UntypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence())
        ));
    }

    @Test
    public void canParseSingletonList() {
        var source = "[42]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, instanceOf(
            UntypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence(isUntypedIntLiteralNode(42)))
        ));
    }

    @Test
    public void canParseListWithMultipleElements() {
        var source = "[42, 47, 52]";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, instanceOf(
            UntypedListLiteralNode.class,
            has("elements", x -> x.elements(), isSequence(
                isUntypedIntLiteralNode(42),
                isUntypedIntLiteralNode(47),
                isUntypedIntLiteralNode(52)
            ))
        ));
    }
}
