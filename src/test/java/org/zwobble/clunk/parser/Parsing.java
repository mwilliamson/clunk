package org.zwobble.clunk.parser;

import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.tokeniser.TokenIterator;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Parsing {
    public static <T> T parseString(
        String sourceContents,
        BiFunction<Parser, TokenIterator<TokenType>, T> parseTokens
    ) {
        var source = FileFragmentSource.create("<string>", sourceContents);
        var tokens = Tokeniser.tokenise(source);
        var parser =  new Parser(source);
        return parseTokens.apply(parser, tokens);
    }
}
