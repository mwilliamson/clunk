package org.zwobble.clunk.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class RegexTokeniserTests {
    enum TokenType {
        UNKNOWN,
        KEYWORD,
        IDENTIFIER,
        WHITESPACE
    }

    @Test
    public void emptyStringIsTokenisedToNoTokens() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of());

        var result = tokeniser.tokenise("");

        assertThat(result, empty());
    }

    @Test
    public void regexIsAppliedToGenerateToken() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));

        var result = tokeniser.tokenise("count");

        assertThat(result, contains(
            new Token<>(0, TokenType.IDENTIFIER, "count")
        ));
    }

    @Test
    public void firstMatchingRegexIsUsed() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.KEYWORD, "if"),
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));

        var result = tokeniser.tokenise("if");

        assertThat(result, contains(
            new Token<>(0, TokenType.KEYWORD, "if")
        ));
    }

    @Test
    public void sequenceOfTokensCanBeGenerated() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+"),
            RegexTokeniser.rule(TokenType.WHITESPACE, "\\s")
        ));

        var result = tokeniser.tokenise("one two");

        assertThat(result, contains(
            new Token<>(0, TokenType.IDENTIFIER, "one"),
            new Token<>(3, TokenType.WHITESPACE, " "),
            new Token<>(4, TokenType.IDENTIFIER, "two")
        ));
    }

    @Test
    public void whenNoRulesMatchThenUnknownTokensAreGenerated() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));

        var result = tokeniser.tokenise("!!a!");

        assertThat(result, contains(
            new Token<>(0, TokenType.UNKNOWN, "!"),
            new Token<>(1, TokenType.UNKNOWN, "!"),
            new Token<>(2, TokenType.IDENTIFIER, "a"),
            new Token<>(3, TokenType.UNKNOWN, "!")
        ));
    }
}
