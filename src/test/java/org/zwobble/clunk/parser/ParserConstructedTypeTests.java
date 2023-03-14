package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedConstructedTypeNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedTypeLevelReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;

public class ParserConstructedTypeTests {
    @Test
    public void canParseConstructedTypeWithOneArgument() {
        var source = "List[String]";

        var node = parseString(source, Parser::parseTypeLevelExpression);

        assertThat(node, isUntypedConstructedTypeNode(
            isUntypedTypeLevelReferenceNode("List"),
            isSequence(isUntypedTypeLevelReferenceNode("String"))
        ));
    }

    @Test
    public void canParseConstructedTypeWithMultipleArguments() {
        var source = "A[B, C, D]";

        var node = parseString(source, Parser::parseTypeLevelExpression);

        assertThat(node, isUntypedConstructedTypeNode(
            isUntypedTypeLevelReferenceNode("A"),
            isSequence(
                isUntypedTypeLevelReferenceNode("B"),
                isUntypedTypeLevelReferenceNode("C"),
                isUntypedTypeLevelReferenceNode("D")
            )
        ));
    }
}
