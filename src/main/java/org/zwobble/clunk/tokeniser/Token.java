package org.zwobble.clunk.tokeniser;

import org.zwobble.clunk.sources.Source;

public record Token<T>(Source source, T tokenType, String value) {
    public static <T> Token<T> token(Source source, T tokenType, String value) {
        return new Token<T>(source, tokenType, value);
    }

    public String describe() {
        return tokenType + ": " + value;
    }
}
