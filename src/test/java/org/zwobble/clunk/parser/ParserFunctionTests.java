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
    public void canParseFunctionWithNoArgs() {
        var source = "fun f() -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasArgs(empty())
        ));
    }

    @Test
    public void canParseFunctionWithSingleArg() {
        var source = "fun f(x: Int) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasArgs(contains(
                isUntypedArgNode(
                    untypedArgNodeHasName("x"),
                    untypedArgNodeHasType(isUntypedStaticReferenceNode("Int"))
                )
            ))
        ));
    }

    @Test
    public void canParseFunctionWithMultipleArgs() {
        var source = "fun f(x: Int, y: String) -> String { }";

        var node = parseString(source, Parser::parseNamespaceStatement);

        assertThat(node, isUntypedFunctionNode(
            untypedFunctionNodeHasArgs(contains(
                isUntypedArgNode(
                    untypedArgNodeHasName("x"),
                    untypedArgNodeHasType(isUntypedStaticReferenceNode("Int"))
                ),
                isUntypedArgNode(
                    untypedArgNodeHasName("y"),
                    untypedArgNodeHasType(isUntypedStaticReferenceNode("String"))
                )
            ))
        ));
    }
}
