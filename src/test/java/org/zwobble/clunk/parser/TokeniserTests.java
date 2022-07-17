package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.sources.FileFragmentSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TokeniserTests {
    @Test
    public void whitespaceTokensAreIgnored() {
        var source = FileFragmentSource.create("x.clunk", """
            1 "two" 3
            """);

        var result = Tokeniser.tokenise(source);

        assertThat(result.nextValue(TokenType.INT), equalTo("1"));
        assertThat(result.nextValue(TokenType.STRING), equalTo("\"two\""));
        assertThat(result.nextValue(TokenType.INT), equalTo("3"));
        assertThat(result.trySkip(TokenType.END), equalTo(true));
    }

    @Test
    public void singleLineCommentsAreIgnored() {
        var source = FileFragmentSource.create("x.clunk", """
            1 // 2 3
            // 4
            5
            """);

        var result = Tokeniser.tokenise(source);

        assertThat(result.nextValue(TokenType.INT), equalTo("1"));
        assertThat(result.nextValue(TokenType.INT), equalTo("5"));
        assertThat(result.trySkip(TokenType.END), equalTo(true));
    }
}
