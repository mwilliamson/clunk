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
            .withReturnType(isUntypedTypeLevelReferenceNode("String"))
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
            isUntypedParamNode().withName("x").withType(isUntypedTypeLevelReferenceNode("Int"))
        )));
    }

    @Test
    public void canParseFunctionWithMultipleParams() {
        var source = "fun f(x: Int, y: String) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withParams(contains(
            isUntypedParamNode().withName("x").withType(isUntypedTypeLevelReferenceNode("Int")),
            isUntypedParamNode().withName("y").withType(isUntypedTypeLevelReferenceNode("String"))
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

    @Test
    public void canParseBlankLinesInBody() {
        var source = """
            fun f() -> String {
                
                one;
                
                
            }
            """;

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode()
            .withBody(contains(
                isUntypedBlankLineNode(),
                isUntypedExpressionStatementNode(isUntypedReferenceNode("one")),
                isUntypedBlankLineNode(),
                isUntypedBlankLineNode()
            ))
        );
    }
}
