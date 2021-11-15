package org.zwobble.clunk.parser;

import org.zwobble.clunk.ast.*;
import org.zwobble.clunk.sources.FileFragmentSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Parser {
    private final String sourceFilename;
    private final String sourceContents;

    public Parser(String sourceFilename, String sourceContents) {
        this.sourceFilename = sourceFilename;
        this.sourceContents = sourceContents;
    }

    public NamespaceStatementNode parseNamespaceStatement(TokenIterator<TokenType> tokens) {
        var recordSource = source(tokens);

        tokens.skip(TokenType.KEYWORD_RECORD);

        var name = tokens.nextValue(TokenType.IDENTIFIER);

        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        var fieldNodes = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_PAREN_CLOSE),
            () -> {
                var fieldSource = source(tokens);

                var fieldName = tokens.nextValue(TokenType.IDENTIFIER);
                tokens.skip(TokenType.SYMBOL_COLON);
                var fieldType = parseType(tokens);

                return new RecordFieldNode(fieldName, fieldType, fieldSource);
            },
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);

        return new RecordNode(name, fieldNodes, recordSource);
    }

    private StaticExpressionNode parseType(TokenIterator<TokenType> tokens) {
        var referenceSource = source(tokens);
        var identifier = tokens.nextValue(TokenType.IDENTIFIER);
        return new StaticReferenceNode(identifier, referenceSource);
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
