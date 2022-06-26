package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedParameterizedTypeNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedTypeLevelReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserParameterizedTypeTests {
    @Test
    public void canParseParameterizedTypeWithOneArgument() {
        var source = "List[String]";

        var node = parseString(source, Parser::parseTypeLevelExpression);

        assertThat(node, isUntypedParameterizedTypeNode(
            isUntypedTypeLevelReferenceNode("List"),
            contains(isUntypedTypeLevelReferenceNode("String"))
        ));
    }

    @Test
    public void canParseParameterizedTypeWithMultipleArguments() {
        var source = "A[B, C, D]";

        var node = parseString(source, Parser::parseTypeLevelExpression);

        assertThat(node, isUntypedParameterizedTypeNode(
            isUntypedTypeLevelReferenceNode("A"),
            contains(
                isUntypedTypeLevelReferenceNode("B"),
                isUntypedTypeLevelReferenceNode("C"),
                isUntypedTypeLevelReferenceNode("D")
            )
        ));
    }
}
