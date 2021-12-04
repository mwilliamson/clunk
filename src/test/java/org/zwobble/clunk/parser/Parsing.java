package org.zwobble.clunk.parser;

import org.zwobble.clunk.tokeniser.TokenIterator;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Parsing {
    public static <T> T parseString(
        String source,
        BiFunction<Parser, TokenIterator<TokenType>, T> parseTokens
    ) {
        var tokens = Tokeniser.tokenise(source);
        var parser =  new Parser("<filename>", source);
        return parseTokens.apply(parser, tokens);
    }
}
