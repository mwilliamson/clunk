package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserFunctionTests {
    @Test
    public void canParseEmptyFunction() {
        var source = "fun f() -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode()
            .withName("f")
            .withReturnType(isUntypedStaticReferenceNode("String"))
        );
    }

    @Test
    public void canParseFunctionWithNoParams() {
        var source = "fun f() -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withParams(empty()));
    }

    @Test
    public void canParseFunctionWithSingleParam() {
        var source = "fun f(x: Int) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withParams(contains(
            isUntypedParamNode().withName("x").withType(isUntypedStaticReferenceNode("Int"))
        )));
    }

    @Test
    public void canParseFunctionWithMultipleParams() {
        var source = "fun f(x: Int, y: String) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withParams(contains(
            isUntypedParamNode().withName("x").withType(isUntypedStaticReferenceNode("Int")),
            isUntypedParamNode().withName("y").withType(isUntypedStaticReferenceNode("String"))
        )));
    }

    @Test
    public void canParseFunctionWithBody() {
        var source = "fun f() -> Bool { return true; return false; }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withBody(contains(
            isUntypedReturnNode().withExpression(isUntypedBoolLiteralNode(true)),
            isUntypedReturnNode().withExpression(isUntypedBoolLiteralNode(false))
        )));
    }
}
