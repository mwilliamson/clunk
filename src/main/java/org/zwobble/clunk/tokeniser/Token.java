package org.zwobble.clunk.tokeniser;

public record Token<T>(int characterIndex, T tokenType, String value) {
    public static <T> Token<T> token(int characterIndex, T tokenType, String value) {
        return new Token<T>(characterIndex, tokenType, value);
    }

    public String describe() {
        return tokenType + ": " + value;
    }
}
