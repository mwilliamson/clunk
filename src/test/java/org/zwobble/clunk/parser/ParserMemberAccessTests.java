package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedMemberAccessNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class ParserMemberAccessTests {
    @Test
    public void canParseMemberAccess() {
        var source = "x.f";

        var node = parseString(source, Parser::parseTopLevelExpression);

        assertThat(node, isUntypedMemberAccessNode(
            isUntypedReferenceNode("x"),
            equalTo("f")
        ));
    }
}
