package org.zwobble.clunk.parser;

import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.tokeniser.TokenIterator;

import java.util.function.BiFunction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class Parsing {
    public static <T> T parseString(
        String sourceContents,
        BiFunction<Parser, TokenIterator<TokenType>, T> parseTokens
    ) {
        var source = FileFragmentSource.create("<string>", sourceContents);
        var tokens = Tokeniser.tokenise(source);
        var parser =  new Parser();
        var result = parseTokens.apply(parser, tokens);
        assertThat(tokens.peek().tokenType(), equalTo(TokenType.END));
        return result;
    }
}
