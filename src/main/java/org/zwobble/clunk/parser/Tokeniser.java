package org.zwobble.clunk.parser;

import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.tokeniser.RegexTokeniser;
import org.zwobble.clunk.tokeniser.Token;
import org.zwobble.clunk.tokeniser.TokenIterator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.zwobble.clunk.tokeniser.Token.token;

public class Tokeniser {
    private static final Map<String, TokenType> KEYWORDS = Map.ofEntries(
        Map.entry("as", TokenType.KEYWORD_AS),
        Map.entry("case", TokenType.KEYWORD_CASE),
        Map.entry("else", TokenType.KEYWORD_ELSE),
        Map.entry("enum", TokenType.KEYWORD_ENUM),
        Map.entry("false", TokenType.KEYWORD_FALSE),
        Map.entry("for", TokenType.KEYWORD_FOR),
        Map.entry("fun", TokenType.KEYWORD_FUN),
        Map.entry("if", TokenType.KEYWORD_IF),
        Map.entry("import", TokenType.KEYWORD_IMPORT),
        Map.entry("in", TokenType.KEYWORD_IN),
        Map.entry("instanceof", TokenType.KEYWORD_INSTANCEOF),
        Map.entry("interface", TokenType.KEYWORD_INTERFACE),
        Map.entry("property", TokenType.KEYWORD_PROPERTY),
        Map.entry("record", TokenType.KEYWORD_RECORD),
        Map.entry("return", TokenType.KEYWORD_RETURN),
        Map.entry("sealed", TokenType.KEYWORD_SEALED),
        Map.entry("switch", TokenType.KEYWORD_SWITCH),
        Map.entry("testSuite", TokenType.KEYWORD_TEST_SUITE),
        Map.entry("test", TokenType.KEYWORD_TEST),
        Map.entry("true", TokenType.KEYWORD_TRUE),
        Map.entry("var", TokenType.KEYWORD_VAR),
        Map.entry("yield", TokenType.KEYWORD_YIELD)
    );

    private static final RegexTokeniser<TokenType> tokeniser = new RegexTokeniser<>(
        TokenType.UNKNOWN,
        List.of(
            RegexTokeniser.rule(TokenType.COMMENT_SINGLE_LINE, "//.*"),

            RegexTokeniser.rule(TokenType.SYMBOL_AMPERSAND_AMPERSAND, "&&"),
            RegexTokeniser.rule(TokenType.SYMBOL_ARROW, "->"),
            RegexTokeniser.rule(TokenType.SYMBOL_BAR_BAR, "\\|\\|"),
            RegexTokeniser.rule(TokenType.SYMBOL_EQUALS_EQUALS, "=="),
            RegexTokeniser.rule(TokenType.SYMBOL_HASH_OPEN, "#\\["),
            RegexTokeniser.rule(TokenType.SYMBOL_NOT_EQUAL, "!="),
            RegexTokeniser.rule(TokenType.SYMBOL_SUBTYPE, "<:"),

            RegexTokeniser.rule(TokenType.SYMBOL_BANG, "!"),
            RegexTokeniser.rule(TokenType.SYMBOL_BRACE_OPEN, "\\{"),
            RegexTokeniser.rule(TokenType.SYMBOL_BRACE_CLOSE, "\\}"),
            RegexTokeniser.rule(TokenType.SYMBOL_COLON, ":"),
            RegexTokeniser.rule(TokenType.SYMBOL_COMMA, ","),
            RegexTokeniser.rule(TokenType.SYMBOL_DOT, "\\."),
            RegexTokeniser.rule(TokenType.SYMBOL_EQUALS, "="),
            RegexTokeniser.rule(TokenType.SYMBOL_FORWARD_SLASH, "/"),
            RegexTokeniser.rule(TokenType.SYMBOL_PAREN_OPEN, "\\("),
            RegexTokeniser.rule(TokenType.SYMBOL_PAREN_CLOSE, "\\)"),
            RegexTokeniser.rule(TokenType.SYMBOL_PLUS, "\\+"),
            RegexTokeniser.rule(TokenType.SYMBOL_SEMICOLON, ";"),
            RegexTokeniser.rule(TokenType.SYMBOL_SQUARE_OPEN, "\\["),
            RegexTokeniser.rule(TokenType.SYMBOL_SQUARE_CLOSE, "\\]"),

            RegexTokeniser.rule(TokenType.BLANK_LINE, "[\n][\r\t ]*(?=[\n])"),
            RegexTokeniser.rule(TokenType.IDENTIFIER, "[A-Za-z][A-Za-z0-9]*"),
            RegexTokeniser.rule(TokenType.INT, "0|-?[1-9][0-9]*"),
            RegexTokeniser.rule(TokenType.STRING, "\"(?:[^\\\\\"\n\r]|\\\\.)*\""),
            RegexTokeniser.rule(TokenType.WHITESPACE, "[\r\n\t ]+")
        )
    );

    public static TokenIterator<TokenType> tokenise(FileFragmentSource source) {
        var tokens = tokeniser.tokenise(source).stream()
            .filter(token -> token.tokenType() != TokenType.WHITESPACE)
            .map(token ->
                token.tokenType() == TokenType.IDENTIFIER
                    ? new Token<>(token.source(), KEYWORDS.getOrDefault(token.value(), token.tokenType()), token.value())
                    : token
            )
            .collect(Collectors.toList());

        return new TokenIterator<>(tokens, token(source.end(), TokenType.END, ""));
    }
}
