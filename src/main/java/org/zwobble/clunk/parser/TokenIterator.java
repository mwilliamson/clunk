package org.zwobble.clunk.parser;

import java.util.List;

public class TokenIterator<TTokenType> {
    private final InfinitelyPaddedIterator<Token<TTokenType>> tokens;

    public TokenIterator(List<Token<TTokenType>> tokens, Token<TTokenType> end) {
        this.tokens = new InfinitelyPaddedIterator<>(tokens, end);
    }

    public void skip(TTokenType tokenType) {
        nextValue(tokenType);
    }

    public String nextValue(TTokenType tokenType) {
        var token = tokens.get();
        if (token.tokenType().equals(tokenType)) {
            tokens.moveNext();
            return token.value();
        } else {
            throw new UnexpectedTokenException(
                tokenType.toString(),
                token.tokenType() + " \"" + token.value() + "\""
            );
        }
    }

    public boolean isNext(TTokenType tokenType) {
        return tokens.get().tokenType().equals(tokenType);
    }

    public boolean trySkip(TTokenType tokenType) {
        if (isNext(tokenType)) {
            tokens.moveNext();
            return true;
        } else {
            return false;
        }
    }
}
