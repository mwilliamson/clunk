package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedTypeLevelReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserTypeLevelReferenceTests {
    @Test
    public void canParseTypeLevelReference() {
        var source = "Person";

        var node = parseString(source, Parser::parseTypeLevelExpression);

        assertThat(node, isUntypedTypeLevelReferenceNode("Person"));
    }
}
