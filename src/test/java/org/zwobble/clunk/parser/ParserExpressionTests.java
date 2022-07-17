package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.tokeniser.UnexpectedTokenException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.parser.Parsing.parseString;

public class ParserExpressionTests {
    @Test
    public void whenExpressionIsNotFoundThenErrorIsThrown() {
        var source = "}";

        var result = assertThrows(UnexpectedTokenException.class, () -> parseString(source, Parser::parseExpression));

        assertThat(result.getExpected(), equalTo("primary expression"));
        assertThat(result.getActual(), equalTo("SYMBOL_BRACE_CLOSE: }"));
    }
}
