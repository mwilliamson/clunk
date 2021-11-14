package org.zwobble.clunk.parser;

import java.util.List;

public class TokenIterator<TTokenType> {
    private final List<Token<TTokenType>> tokens;
    private final Token<TTokenType> end;
    private int index = 0;

    public TokenIterator(List<Token<TTokenType>> tokens, Token<TTokenType> end) {
        this.tokens = tokens;
        this.end = end;
    }

    public void skip(TTokenType tokenType) {
        nextValue(tokenType);
    }

    public String nextValue(TTokenType tokenType) {
        var token = getToken();
        if (token.tokenType().equals(tokenType)) {
            index++;
            return token.value();
        } else {
            throw new UnexpectedTokenException(
                tokenType.toString(),
                token.tokenType() + " \"" + token.value() + "\""
            );
        }
    }

    public boolean isNext(TTokenType tokenType) {
        return getToken().tokenType().equals(tokenType);
    }

    public boolean trySkip(TTokenType tokenType) {
        if (isNext(tokenType)) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    private Token<TTokenType> getToken() {
        if (index < tokens.size()) {
            return tokens.get(index);
        } else {
            return end;
        }
    }
}
