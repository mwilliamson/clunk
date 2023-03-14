package org.zwobble.clunk.tokeniser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.sources.FileFragmentSource;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.isSequence;

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

        assertThat(result, isSequence());
    }

    @Test
    public void regexIsAppliedToGenerateToken() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));
        var source = source("count");

        var result = tokeniser.tokenise(source);

        assertThat(result, isSequence(
            equalTo(new Token<>(source.at(0, 5), TokenType.IDENTIFIER, "count"))
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

        assertThat(result, isSequence(
            equalTo(new Token<>(source.at(0, 2), TokenType.KEYWORD, "if"))
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

        assertThat(result, isSequence(
            equalTo(new Token<>(source.at(0, 3), TokenType.IDENTIFIER, "one")),
            equalTo(new Token<>(source.at(3, 4), TokenType.WHITESPACE, " ")),
            equalTo(new Token<>(source.at(4, 7), TokenType.IDENTIFIER, "two"))
        ));
    }

    @Test
    public void whenNoRulesMatchThenUnknownTokensAreGenerated() {
        var tokeniser = new RegexTokeniser<>(TokenType.UNKNOWN, List.of(
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z0-9]+")
        ));
        var source = source("!!a!");

        var result = tokeniser.tokenise(source);

        assertThat(result, isSequence(
            equalTo(new Token<>(source.at(0, 1), TokenType.UNKNOWN, "!")),
            equalTo(new Token<>(source.at(1, 2), TokenType.UNKNOWN, "!")),
            equalTo(new Token<>(source.at(2, 3), TokenType.IDENTIFIER, "a")),
            equalTo(new Token<>(source.at(3, 4), TokenType.UNKNOWN, "!"))
        ));
    }

    private FileFragmentSource source(String sourceContents) {
        return FileFragmentSource.create("<filename>", sourceContents);
    }
}
