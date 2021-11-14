package org.zwobble.clunk.parser;

import org.zwobble.clunk.ast.NamespaceStatementNode;
import org.zwobble.clunk.ast.RecordFieldNode;
import org.zwobble.clunk.ast.RecordNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class Parser {
    public static NamespaceStatementNode parseNamespaceStatement(TokenIterator<TokenType> tokens) {
        tokens.skip(TokenType.KEYWORD_RECORD);

        var name = tokens.nextValue(TokenType.IDENTIFIER);

        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        var fieldNodes = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_PAREN_CLOSE),
            () -> {
                var fieldName = tokens.nextValue(TokenType.IDENTIFIER);
                tokens.skip(TokenType.SYMBOL_COLON);
                tokens.skip(TokenType.IDENTIFIER);

                return new RecordFieldNode(fieldName);
            },
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);

        return new RecordNode(name, fieldNodes);
    }

    private static <T> List<T> parseMany(BooleanSupplier stop, Supplier<T> parseElement, BooleanSupplier parseSeparator) {
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
}
