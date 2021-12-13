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

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasName("f"),
            untypedFunctionNodeHasReturnType(isUntypedStaticReferenceNode("String"))
        ));
    }

    @Test
    public void canParseFunctionWithNoParams() {
        var source = "fun f() -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasParams(empty())
        ));
    }

    @Test
    public void canParseFunctionWithSingleParam() {
        var source = "fun f(x: Int) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasParams(contains(
                isUntypedParamNode(
                    untypedParamNodeHasName("x"),
                    untypedParamNodeHasType(isUntypedStaticReferenceNode("Int"))
                )
            ))
        ));
    }

    @Test
    public void canParseFunctionWithMultipleParams() {
        var source = "fun f(x: Int, y: String) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasParams(contains(
                isUntypedParamNode(
                    untypedParamNodeHasName("x"),
                    untypedParamNodeHasType(isUntypedStaticReferenceNode("Int"))
                ),
                isUntypedParamNode(
                    untypedParamNodeHasName("y"),
                    untypedParamNodeHasType(isUntypedStaticReferenceNode("String"))
                )
            ))
        ));
    }
}
