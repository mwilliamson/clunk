package org.zwobble.clunk.parser;

import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.tokeniser.TokenIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Parser {
    private final String sourceFilename;
    private final String sourceContents;

    public Parser(String sourceFilename, String sourceContents) {
        this.sourceFilename = sourceFilename;
        this.sourceContents = sourceContents;
    }

    public UntypedExpressionNode parseExpression(TokenIterator<TokenType> tokens) {
        return parseStringLiteral(tokens);
    }

    private UntypedExpressionNode parseStringLiteral(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        var tokenValue = tokens.nextValue(TokenType.STRING);
        var escapedValue = tokenValue.substring(1, tokenValue.length() - 1);
        var unescapedValue = unescape(escapedValue, source);
        return new UntypedStringLiteralNode(unescapedValue, source);
    }

    private static final Pattern STRING_ESCAPE_PATTERN = Pattern.compile("\\\\(.)");

    private String unescape(String value, Source source) {
        var matcher = STRING_ESCAPE_PATTERN.matcher(value);
        var result = new StringBuilder();
        var lastIndex = 0;
        while (matcher.find()) {
            result.append(value.subSequence(lastIndex, matcher.start()));
            var code = matcher.group(1);
            result.append(unescapeCharacter(code, source.at(matcher.start())));
            lastIndex = matcher.end();
        }
        result.append(value.subSequence(lastIndex, value.length()));
        return result.toString();
    }

    private char unescapeCharacter(String code, Source source) {
        return switch (code) {
            case "n" -> '\n';
            case "r" -> '\r';
            case "t" -> '\t';
            case "\"" -> '"';
            case "\\" -> '\\';
            default -> throw new UnrecognisedEscapeSequenceError("\\" + code, source);
        };
    }

    public UntypedNamespaceNode parseNamespace(TokenIterator<TokenType> tokens, List<String> name) {
        var source = source(tokens);

        var statements = parseMany(
            () -> tokens.isNext(TokenType.END),
            () -> parseNamespaceStatement(tokens),
            () -> true
        );

        return new UntypedNamespaceNode(name, statements, source);
    }

    public UntypedNamespaceStatementNode parseNamespaceStatement(TokenIterator<TokenType> tokens) {
        var recordSource = source(tokens);

        tokens.skip(TokenType.KEYWORD_RECORD);

        var name = tokens.nextValue(TokenType.IDENTIFIER);

        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        var fieldNodes = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_PAREN_CLOSE),
            () -> parseRecordField(tokens),
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);

        return new UntypedRecordNode(name, fieldNodes, recordSource);
    }

    private UntypedRecordFieldNode parseRecordField(TokenIterator<TokenType> tokens) {
        var fieldSource = source(tokens);

        var fieldName = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_COLON);
        var fieldType = parseType(tokens);

        return new UntypedRecordFieldNode(fieldName, fieldType, fieldSource);
    }

    private UntypedStaticExpressionNode parseType(TokenIterator<TokenType> tokens) {
        var referenceSource = source(tokens);
        var identifier = tokens.nextValue(TokenType.IDENTIFIER);
        return new UntypedStaticReferenceNode(identifier, referenceSource);
    }

    private <T> List<T> parseMany(BooleanSupplier stop, Supplier<T> parseElement, BooleanSupplier parseSeparator) {
        var values = new ArrayList<T>();

        while (true) {
            if (stop.getAsBoolean()) {
                return values;
            }

            var element = parseElement.get();
            values.add(element);
            if (!parseSeparator.getAsBoolean()) {
                return values;
            }
        }
    }

    private Source source(TokenIterator<?> tokens) {
        var characterIndex = tokens.peek().characterIndex();
        return new FileFragmentSource(sourceFilename, sourceContents, characterIndex);
    }
}
