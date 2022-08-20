package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedMemberAccessNode;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.isUntypedReferenceNode;
import static org.zwobble.clunk.parser.Parsing.parseString;

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
