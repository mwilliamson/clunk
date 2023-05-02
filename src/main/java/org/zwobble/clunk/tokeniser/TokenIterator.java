package org.zwobble.clunk.tokeniser;

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
                token.tokenType() + " \"" + token.value() + "\"",
                token.source()
            );
        }
    }

    public boolean isNext(TTokenType tokenType) {
        return tokens.get().tokenType().equals(tokenType);
    }

    public boolean isNext(TTokenType tokenType1, TTokenType tokenType2) {
        return tokens.get().tokenType().equals(tokenType1) &&
            tokens.get(1).tokenType().equals(tokenType2);
    }

    public boolean trySkip(TTokenType tokenType) {
        if (isNext(tokenType)) {
            tokens.moveNext();
            return true;
        } else {
            return false;
        }
    }

    public Token<TTokenType> peek() {
        return tokens.get();
    }
}
