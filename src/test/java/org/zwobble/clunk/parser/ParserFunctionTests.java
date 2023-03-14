package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.untyped.UntypedNodeMatchers.*;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;

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

        assertThat(node, isUntypedFunctionNode().withPositionalParams(isSequence()).withNamedParams(isSequence()));
    }

    @Test
    public void canParseFunctionWithSinglePositionalParam() {
        var source = "fun f(x: Int) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withPositionalParams(isSequence(
            isUntypedParamNode().withName("x").withType(isUntypedTypeLevelReferenceNode("Int"))
        )));
    }

    @Test
    public void canParseFunctionWithMultiplePositionalParams() {
        var source = "fun f(x: Int, y: String) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withPositionalParams(isSequence(
            isUntypedParamNode().withName("x").withType(isUntypedTypeLevelReferenceNode("Int")),
            isUntypedParamNode().withName("y").withType(isUntypedTypeLevelReferenceNode("String"))
        )));
    }

    @Test
    public void canParseFunctionWithSingleNamedParam() {
        var source = "fun f(.x: Int) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode()
            .withPositionalParams(isSequence())
            .withNamedParams(isSequence(
                isUntypedParamNode().withName("x").withType(isUntypedTypeLevelReferenceNode("Int"))
            ))
        );
    }

    @Test
    public void canParseFunctionWithMultipleNamedParams() {
        var source = "fun f(.x: Int, .y: String) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode()
            .withPositionalParams(isSequence())
            .withNamedParams(isSequence(
                isUntypedParamNode().withName("x").withType(isUntypedTypeLevelReferenceNode("Int")),
                isUntypedParamNode().withName("y").withType(isUntypedTypeLevelReferenceNode("String"))
            ))
        );
    }

    @Test
    public void canParseNamedParamAfterPositionalParam() {
        var source = "fun f(x: Int, .y: String) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode()
            .withPositionalParams(isSequence(
                isUntypedParamNode().withName("x").withType(isUntypedTypeLevelReferenceNode("Int"))
            ))
            .withNamedParams(isSequence(
                isUntypedParamNode().withName("y").withType(isUntypedTypeLevelReferenceNode("String"))
            ))
        );
    }

    @Test
    public void cannotParsePositionalParamAfterNamedParam() {
        var source = "fun f(.y: String, x: Int) -> String { }";

        assertThrows(
            PositionalParamAfterNamedParamError.class,
            () -> parseString(source, Parser::parseNamespaceStatement)
        );
    }

    @Test
    public void canParseFunctionWithBody() {
        var source = "fun f() -> Bool { return true; return false; }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode().withBody(isSequence(
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
            .withBody(isSequence(
                isUntypedBlankLineNode(),
                isUntypedExpressionStatementNode(isUntypedReferenceNode("one")),
                isUntypedBlankLineNode(),
                isUntypedBlankLineNode()
            ))
        );
    }
}
