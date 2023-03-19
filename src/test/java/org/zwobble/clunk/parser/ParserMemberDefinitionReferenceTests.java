package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class ParserMemberDefinitionReferenceTests {
    @Test
    public void canParseMemberDefinitionReference() {
        var source = "X::f";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedMemberDefinitionReferenceNode(
            isUntypedReferenceNode("X"),
            equalTo("f")
        ));
    }
}
