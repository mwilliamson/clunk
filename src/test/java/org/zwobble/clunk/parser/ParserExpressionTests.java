package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.tokeniser.UnexpectedTokenException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.parser.Parsing.parseString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class ParserExpressionTests {
    @Test
    public void whenExpressionIsNotFoundThenErrorIsThrown() {
        var source = "}";

        var result = assertThrows(UnexpectedTokenException.class, () -> parseString(source, Parser::parseTopLevelExpression));

        assertThat(result.getExpected(), equalTo("primary expression"));
        assertThat(result.getActual(), equalTo("SYMBOL_BRACE_CLOSE: }"));
    }
}
