package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedConstructedTypeNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedTypeLevelReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserConstructedTypeTests {
    @Test
    public void canParseConstructedTypeWithOneArgument() {
        var source = "List[String]";

        var node = parseString(source, Parser::parseTypeLevelExpression);

        assertThat(node, isUntypedConstructedTypeNode(
            isUntypedTypeLevelReferenceNode("List"),
            contains(isUntypedTypeLevelReferenceNode("String"))
        ));
    }

    @Test
    public void canParseConstructedTypeWithMultipleArguments() {
        var source = "A[B, C, D]";

        var node = parseString(source, Parser::parseTypeLevelExpression);

        assertThat(node, isUntypedConstructedTypeNode(
            isUntypedTypeLevelReferenceNode("A"),
            contains(
                isUntypedTypeLevelReferenceNode("B"),
                isUntypedTypeLevelReferenceNode("C"),
                isUntypedTypeLevelReferenceNode("D")
            )
        ));
    }
}
