package org.zwobble.clunk.tokeniser;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.sources.Source;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.zwobble.clunk.tokeniser.Token.token;

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
        var source = source("");

        var result = tokeniser.tokenise(source);

        assertThat(result, empty());
    }

    @Test
    public void regexIsAppliedToGenerateToken() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));
        var source = source("count");

        var result = tokeniser.tokenise(source);

        assertThat(result, contains(
            new Token<>(source.at(0, 5), TokenType.IDENTIFIER, "count")
        ));
    }

    @Test
    public void firstMatchingRegexIsUsed() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.KEYWORD, "if"),
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));
        var source = source("if");

        var result = tokeniser.tokenise(source);

        assertThat(result, contains(
            new Token<>(source.at(0, 2), TokenType.KEYWORD, "if")
        ));
    }

    @Test
    public void sequenceOfTokensCanBeGenerated() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+"),
            RegexTokeniser.rule(TokenType.WHITESPACE, "\\s")
        ));
        var source = source("one two");

        var result = tokeniser.tokenise(source);

        assertThat(result, contains(
            new Token<>(source.at(0, 3), TokenType.IDENTIFIER, "one"),
            new Token<>(source.at(3, 4), TokenType.WHITESPACE, " "),
            new Token<>(source.at(4, 7), TokenType.IDENTIFIER, "two")
        ));
    }

    @Test
    public void whenNoRulesMatchThenUnknownTokensAreGenerated() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));
        var source = source("!!a!");

        var result = tokeniser.tokenise(source);

        assertThat(result, contains(
            new Token<>(source.at(0, 1), TokenType.UNKNOWN, "!"),
            new Token<>(source.at(1, 2), TokenType.UNKNOWN, "!"),
            new Token<>(source.at(2, 3), TokenType.IDENTIFIER, "a"),
            new Token<>(source.at(3, 4), TokenType.UNKNOWN, "!")
        ));
    }

    private FileFragmentSource source(String sourceContents) {
        return FileFragmentSource.create("<filename>", sourceContents);
    }
}
