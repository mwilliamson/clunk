package org.zwobble.clunk.parser;

import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.tokeniser.RegexTokeniser;
import org.zwobble.clunk.tokeniser.TokenIterator;

import java.util.List;
import java.util.stream.Collectors;

import static org.zwobble.clunk.tokeniser.Token.token;

public class Tokeniser {
    private static final RegexTokeniser<TokenType> tokeniser = new RegexTokeniser<>(
        TokenType.UNKNOWN,
        List.of(
            RegexTokeniser.rule(TokenType.COMMENT_SINGLE_LINE, "//.*"),

            RegexTokeniser.rule(TokenType.KEYWORD_CASE, "case"),
            RegexTokeniser.rule(TokenType.KEYWORD_ELSE, "else"),
            RegexTokeniser.rule(TokenType.KEYWORD_ENUM, "enum"),
            RegexTokeniser.rule(TokenType.KEYWORD_FALSE, "false"),
            RegexTokeniser.rule(TokenType.KEYWORD_FUN, "fun"),
            RegexTokeniser.rule(TokenType.KEYWORD_IF, "if"),
            RegexTokeniser.rule(TokenType.KEYWORD_IMPORT, "import"),
            RegexTokeniser.rule(TokenType.KEYWORD_INTERFACE, "interface"),
            RegexTokeniser.rule(TokenType.KEYWORD_PROPERTY, "property"),
            RegexTokeniser.rule(TokenType.KEYWORD_RECORD, "record"),
            RegexTokeniser.rule(TokenType.KEYWORD_RETURN, "return"),
            RegexTokeniser.rule(TokenType.KEYWORD_SEALED, "sealed"),
            RegexTokeniser.rule(TokenType.KEYWORD_SWITCH, "switch"),
            RegexTokeniser.rule(TokenType.KEYWORD_TEST, "test"),
            RegexTokeniser.rule(TokenType.KEYWORD_TRUE, "true"),
            RegexTokeniser.rule(TokenType.KEYWORD_VAR, "var"),
            RegexTokeniser.rule(TokenType.KEYWORD_YIELD, "yield"),

            RegexTokeniser.rule(TokenType.SYMBOL_ARROW, "->"),
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
            RegexTokeniser.rule(TokenType.SYMBOL_SUBTYPE, "<:"),

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
            .collect(Collectors.toList());

        return new TokenIterator<>(tokens, token(source.end(), TokenType.END, ""));
    }
}
