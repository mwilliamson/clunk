package org.zwobble.clunk.tokeniser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.sources.NullSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TokenIteratorTests {
    enum TokenType {
        IDENTIFIER,
        SYMBOL,
        END
    }

    @Test
    public void skipSucceedsWhenNextTokenHasMatchingTokenType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);
        tokens.skip(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL);
        tokens.skip(TokenType.END);
        tokens.skip(TokenType.END);
    }

    @Test
    public void skipFailsWhenNextTokenDoesNotHaveMatchingTokenType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        try {
            tokens.skip(TokenType.SYMBOL);
            fail("expected exception");
        } catch (UnexpectedTokenException exception) {
            assertThat(exception.getMessage(), equalTo("Expected: SYMBOL\nBut got: IDENTIFIER \"x\""));
        }
    }

    @Test
    public void trySkipAdvancesIteratorAndReturnsTrueWhenNextTokenHasMatchingTokenType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        assertThat(tokens.trySkip(TokenType.SYMBOL), equalTo(true));
        assertThat(tokens.trySkip(TokenType.IDENTIFIER), equalTo(true));
        assertThat(tokens.trySkip(TokenType.SYMBOL), equalTo(true));
        assertThat(tokens.trySkip(TokenType.END), equalTo(true));
        assertThat(tokens.trySkip(TokenType.END), equalTo(true));
    }

    @Test
    public void trySkipDoesNotAdvanceIteratorAndReturnsFalseWhenNextTokenDoesNotHaveMatchingTokenType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        assertThat(tokens.trySkip(TokenType.SYMBOL), equalTo(false));
        assertThat(tokens.trySkip(TokenType.IDENTIFIER), equalTo(true));
    }

    @Test
    public void nextValueReturnsValueWhenCurrentTokenHasMatchingTokenType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        assertThat(tokens.nextValue(TokenType.SYMBOL), equalTo("!"));
        assertThat(tokens.nextValue(TokenType.IDENTIFIER), equalTo("x"));
    }

    @Test
    public void nextValueFailsWhenCurrentTokenDoesNotHaveMatchingTokenType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        try {
            tokens.nextValue(TokenType.SYMBOL);
            fail("expected exception");
        } catch (UnexpectedTokenException exception) {
            assertThat(exception.getMessage(), equalTo("Expected: SYMBOL\nBut got: IDENTIFIER \"x\""));
        }
    }

    @Test
    public void isNextWithOneArgReturnsTrueIfNextTokenIsOfMatchingType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        assertThat(tokens.isNext(TokenType.IDENTIFIER), equalTo(true));
    }

    @Test
    public void isNextWithOneArgReturnsFalseIfNextTokenIsNotOfMatchingType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        assertThat(tokens.isNext(TokenType.SYMBOL), equalTo(false));
    }

    @Test
    public void isNextWithTwoArgReturnsTrueIfBothNextTokensMatchRespectiveTypes() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        assertThat(tokens.isNext(TokenType.IDENTIFIER, TokenType.SYMBOL), equalTo(true));
    }

    @Test
    public void isNextWithTwoArgReturnsFalseIfFirstTokenDoesNotMatchRespectiveType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        assertThat(tokens.isNext(TokenType.SYMBOL, TokenType.SYMBOL), equalTo(false));
    }

    @Test
    public void isNextWithTwoArgReturnsFalseIfSecondTokenDoesNotMatchRespectiveType() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        tokens.skip(TokenType.SYMBOL);

        assertThat(tokens.isNext(TokenType.IDENTIFIER, TokenType.IDENTIFIER), equalTo(false));
    }

    @Test
    public void peekReturnsNextTokenWithoutAdvancing() {
        var tokens = new TokenIterator<>(
            List.of(
                token(0, TokenType.SYMBOL, "!"),
                token(1, TokenType.IDENTIFIER, "x"),
                token(2, TokenType.SYMBOL, "!")
            ),
            token(3, TokenType.END, "")
        );

        assertThat(tokens.peek(), equalTo(token(0, TokenType.SYMBOL, "!")));
        assertThat(tokens.peek(), equalTo(token(0, TokenType.SYMBOL, "!")));
        tokens.skip(TokenType.SYMBOL);
        assertThat(tokens.peek(), equalTo(token(1, TokenType.IDENTIFIER, "x")));
    }

    private Token<TokenType> token(int characterIndex, TokenType tokenType, String value) {
        var source = NullSource.INSTANCE;
        return new Token<>(source, tokenType, value);
    }
}
