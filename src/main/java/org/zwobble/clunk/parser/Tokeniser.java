package org.zwobble.clunk.parser;

import java.util.List;
import java.util.stream.Collectors;

import static org.zwobble.clunk.parser.Token.token;

public class Tokeniser {
    private static final RegexTokeniser<TokenType> tokeniser = new RegexTokeniser<>(
        TokenType.UNKNOWN,
        List.of(
            RegexTokeniser.rule(TokenType.KEYWORD_RECORD, "record"),

            RegexTokeniser.rule(TokenType.SYMBOL_COLON, ":"),
            RegexTokeniser.rule(TokenType.SYMBOL_COMMA, ","),
            RegexTokeniser.rule(TokenType.SYMBOL_PAREN_OPEN, "\\("),
            RegexTokeniser.rule(TokenType.SYMBOL_PAREN_CLOSE, "\\)"),

            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z][A-Za-z0-9]*"),
            RegexTokeniser.rule(TokenType.WHITESPACE, "[\r\n\t ]+")
        )
    );

    public static TokenIterator<TokenType> tokenise(String source) {
        var tokens = tokeniser.tokenise(source).stream()
            .filter(token -> token.tokenType() != TokenType.WHITESPACE)
            .collect(Collectors.toList());

        return new TokenIterator<>(tokens, token(source.length(), TokenType.END, ""));
    }
}
