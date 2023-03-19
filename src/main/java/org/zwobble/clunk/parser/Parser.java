package org.zwobble.clunk.parser;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.tokeniser.TokenIterator;
import org.zwobble.clunk.tokeniser.UnexpectedTokenException;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.NamespaceName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    private class ParseAdd implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.ADDITION;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var right = parseSubexpression(tokens, this);
            return new UntypedAddNode(left, right, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_PLUS;
        }
    }

    private UntypedBlankLineNode parseBlankLine(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        tokens.skip(TokenType.BLANK_LINE);
        return new UntypedBlankLineNode(source);
    }

    private List<UntypedFunctionStatementNode> parseBlock(TokenIterator<TokenType> tokens) {
        tokens.skip(TokenType.SYMBOL_BRACE_OPEN);
        var body = parseRepeated(
            () -> tokens.isNext(TokenType.SYMBOL_BRACE_CLOSE),
            () -> parseFunctionStatement(tokens)
        );
        tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);
        return body;
    }

    private class ParseCall implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.CALL;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var args = parseCallArgs(tokens);
            tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
            return new UntypedCallNode(left, List.of(), args, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_PAREN_OPEN;
        }
    }

    private class ParseCallWithTypeLevelArgs implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.CALL;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var typeLevelArgs = parseMany(
                () -> tokens.isNext(TokenType.SYMBOL_SQUARE_CLOSE),
                () -> parseTypeLevelExpression(tokens),
                () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
            );
            tokens.skip(TokenType.SYMBOL_SQUARE_CLOSE);

            tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
            var positionalArgs = parseCallArgs(tokens);
            tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);

            return new UntypedCallNode(left, typeLevelArgs, positionalArgs, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_SQUARE_OPEN;
        }
    }

    private UntypedArgsNode parseCallArgs(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        var positional = new ArrayList<UntypedExpressionNode>();
        var named = new ArrayList<UntypedNamedArgNode>();

        parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_PAREN_CLOSE),
            () -> {
                var argSource = source(tokens);
                if (tokens.trySkip(TokenType.SYMBOL_DOT)) {
                    var argName = tokens.nextValue(TokenType.IDENTIFIER);
                    tokens.skip(TokenType.SYMBOL_EQUALS);
                    var expression = parseTopLevelExpression(tokens);
                    named.add(new UntypedNamedArgNode(argName, expression, argSource));
                } else if (named.isEmpty()) {
                    var expression = parseTopLevelExpression(tokens);
                    positional.add(expression);
                } else {
                    throw new PositionalArgAfterNamedArgError(argSource);
                }
                return null;
            },
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );

        return new UntypedArgsNode(positional, named, source);
    }

    private class ParseCast implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.CAST;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var typeExpression = parseTypeLevelExpression(tokens);

            return new UntypedCastUnsafeNode(left, typeExpression, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.KEYWORD_AS;
        }
    }

    private UntypedNamespaceStatementNode parseEnum(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        tokens.skip(TokenType.KEYWORD_ENUM);
        var name = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_BRACE_OPEN);
        var members = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_BRACE_CLOSE),
            () -> tokens.nextValue(TokenType.IDENTIFIER),
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );
        tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);
        return new UntypedEnumNode(name, members, source);
    }

    private class ParseEquals implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.EQUALITY;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var right = parseSubexpression(tokens, this);
            return new UntypedEqualsNode(left, right, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_EQUALS_EQUALS;
        }
    }

    public UntypedExpressionNode parseTopLevelExpression(TokenIterator<TokenType> tokens) {
        return parseExpression(tokens, Optional.empty());
    }

    private UntypedExpressionNode parseSubexpression(TokenIterator<TokenType> tokens, OperatorParselet parent) {
        return parseSubexpression(tokens, parent.precedence());
    }

    private UntypedExpressionNode parseSubexpression(TokenIterator<TokenType> tokens, OperatorPrecedence parentPrecedence) {
        return parseExpression(tokens, Optional.of(parentPrecedence));
    }

    private UntypedExpressionNode parseExpression(TokenIterator<TokenType> tokens, Optional<OperatorPrecedence> parentPrecedence) {
        var expression = tokens.isNext(TokenType.SYMBOL_BANG)
            ? parseLogicalNot(tokens)
            : parsePrimaryExpression(tokens);

        var parentPrecedenceOrdinal = parentPrecedence.map(p -> p.ordinal()).orElse(-1);

        while (true) {
            var operator = parseOperator(tokens);
            if (operator.isEmpty() || operator.get().precedence().ordinal() <= parentPrecedenceOrdinal) {
                return expression;
            }
            var operatorSource = source(tokens);
            tokens.skip(operator.get().tokenType());
            expression = operator.get().parse(expression, tokens, operatorSource);
        }
    }

    private final Map<TokenType, OperatorParselet> operatorParselets = Stream.of(
        new ParseAdd(),
        new ParseCall(),
        new ParseCallWithTypeLevelArgs(),
        new ParseCast(),
        new ParseEquals(),
        new ParseInstanceOf(),
        new ParseLogicalAnd(),
        new ParseLogicalOr(),
        new ParseMemberAccess(),
        new ParseMemberDefinitionReferenceAccess(),
        new ParseNotEqual()
    ).collect(Collectors.toMap(
            parselet -> parselet.tokenType(),
            parselet -> parselet
        ));

    private Optional<OperatorParselet> parseOperator(TokenIterator<TokenType> tokens) {
        return Optional.ofNullable(operatorParselets.get(tokens.peek().tokenType()));
    }

    private UntypedExpressionNode parsePrimaryExpression(TokenIterator<TokenType> tokens) {
        var source = tokens.peek().source();
        if (tokens.trySkip(TokenType.KEYWORD_FALSE)) {
            return new UntypedBoolLiteralNode(false, source);
        } else if (tokens.trySkip(TokenType.KEYWORD_TRUE)) {
            return new UntypedBoolLiteralNode(true, source);
        } else if (tokens.isNext(TokenType.STRING)) {
            return parseStringLiteral(tokens);
        } else if (tokens.isNext(TokenType.INT)) {
            return parseIntLiteral(tokens);
        } else if (tokens.isNext(TokenType.IDENTIFIER)) {
            return parseReference(tokens);
        } else if (tokens.trySkip(TokenType.SYMBOL_PAREN_OPEN)) {
            var expression = parseTopLevelExpression(tokens);
            tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
            return expression;
        } else if (tokens.isNext(TokenType.SYMBOL_SQUARE_OPEN)) {
            return parseListLiteral(tokens);
        } else if (tokens.isNext(TokenType.SYMBOL_HASH_OPEN)) {
            return parseMapLiteral(tokens);
        } else {
            throw new UnexpectedTokenException("primary expression", tokens.peek().describe(), source);
        }
    }

    private UntypedExpressionStatementNode parseExpressionStatement(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        var expression = parseTopLevelExpression(tokens);
        tokens.skip(TokenType.SYMBOL_SEMICOLON);
        return new UntypedExpressionStatementNode(expression, source);
    }

    private UntypedForEachNode parseForEach(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_FOR);
        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        tokens.skip(TokenType.KEYWORD_VAR);
        var targetName = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.KEYWORD_IN);
        var iterable = parseTopLevelExpression(tokens);
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
        var body = parseBlock(tokens);

        return new UntypedForEachNode(targetName, iterable, body, source);
    }

    private class ParseInstanceOf implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.RELATIONAL;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var type = parseTypeLevelExpression(tokens);
            return new UntypedInstanceOfNode(left, type, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.KEYWORD_INSTANCEOF;
        }
    }

    private UntypedExpressionNode parseIntLiteral(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        var tokenValue = tokens.nextValue(TokenType.INT);
        var value = Integer.parseInt(tokenValue);
        return new UntypedIntLiteralNode(value, source);
    }

    private UntypedStringLiteralNode parseStringLiteral(TokenIterator<TokenType> tokens) {
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
            result.append(unescapeCharacter(code, source.at(matcher.start(), matcher.end())));
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

    private UntypedListLiteralNode parseListLiteral(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.SYMBOL_SQUARE_OPEN);

        var elements = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_SQUARE_CLOSE),
            () -> parseTopLevelExpression(tokens),
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );

        tokens.skip(TokenType.SYMBOL_SQUARE_CLOSE);

        return new UntypedListLiteralNode(elements, source);
    }

    private class ParseLogicalAnd implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.LOGICAL_AND;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var right = parseSubexpression(tokens, this);
            return new UntypedLogicalAndNode(left, right, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_AMPERSAND_AMPERSAND;
        }
    }

    private UntypedMapLiteralNode parseMapLiteral(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.SYMBOL_HASH_OPEN);

        var entries = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_SQUARE_CLOSE),
            () -> parseMapEntryLiteral(tokens),
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );

        tokens.skip(TokenType.SYMBOL_SQUARE_CLOSE);

        return new UntypedMapLiteralNode(entries, source);
    }

    private UntypedMapEntryLiteralNode parseMapEntryLiteral(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.SYMBOL_SQUARE_OPEN);
        var key = parseTopLevelExpression(tokens);
        tokens.skip(TokenType.SYMBOL_COMMA);
        var value = parseTopLevelExpression(tokens);
        tokens.skip(TokenType.SYMBOL_SQUARE_CLOSE);

        return new UntypedMapEntryLiteralNode(key, value, source);
    }

    private UntypedLogicalNotNode parseLogicalNot(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        tokens.skip(TokenType.SYMBOL_BANG);
        var operand = parseSubexpression(tokens, OperatorPrecedence.PREFIX);
        return new UntypedLogicalNotNode(operand, source);
    }

    private class ParseNotEqual implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.EQUALITY;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var right = parseSubexpression(tokens, this);
            return new UntypedNotEqualNode(left, right, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_NOT_EQUAL;
        }
    }

    private class ParseLogicalOr implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.LOGICAL_OR;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var right = parseSubexpression(tokens, this);
            return new UntypedLogicalOrNode(left, right, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_BAR_BAR;
        }
    }

    private static class ParseMemberAccess implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.CALL;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var fieldName = tokens.nextValue(TokenType.IDENTIFIER);
            return new UntypedMemberAccessNode(left, fieldName, operatorSource, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_DOT;
        }
    }

    private static class ParseMemberDefinitionReferenceAccess implements OperatorParselet {
        @Override
        public OperatorPrecedence precedence() {
            return OperatorPrecedence.CALL;
        }

        @Override
        public UntypedExpressionNode parse(
            UntypedExpressionNode left,
            TokenIterator<TokenType> tokens,
            Source operatorSource
        ) {
            var fieldName = tokens.nextValue(TokenType.IDENTIFIER);
            return new UntypedMemberDefinitionReferenceNode(left, fieldName, operatorSource, left.source());
        }

        @Override
        public TokenType tokenType() {
            return TokenType.SYMBOL_COLON_COLON;
        }
    }

    public UntypedNamespaceNode parseNamespace(TokenIterator<TokenType> tokens, NamespaceName name, SourceType sourceType) {
        var source = source(tokens);

        var imports = parseMany(
            () -> !tokens.isNext(TokenType.KEYWORD_IMPORT),
            () -> parseImport(tokens),
            () -> true
        );

        var statements = parseMany(
            () -> tokens.isNext(TokenType.END),
            () -> parseNamespaceStatement(tokens),
            () -> true
        );

        var id = new NamespaceId(name, sourceType);

        return new UntypedNamespaceNode(id, imports, statements, source);
    }

    private UntypedImportNode parseImport(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_IMPORT);

        var namespaceName = parseNamespaceName(tokens);

        var fieldName = tokens.trySkip(TokenType.SYMBOL_DOT)
            ? Optional.of(tokens.nextValue(TokenType.IDENTIFIER))
            : Optional.<String>empty();

        tokens.skip(TokenType.SYMBOL_SEMICOLON);

        return new UntypedImportNode(namespaceName, fieldName, source);
    }

    private NamespaceName parseNamespaceName(TokenIterator<TokenType> tokens) {
        var name = new ArrayList<String>();

        while (true) {
            name.add(tokens.nextValue(TokenType.IDENTIFIER));

            if (!tokens.trySkip(TokenType.SYMBOL_FORWARD_SLASH)) {
                return new NamespaceName(name);
            }
        }
    }

    private UntypedFunctionNode parseFunction(TokenIterator<TokenType> tokens) {
        var source = tokens.peek().source();

        tokens.skip(TokenType.KEYWORD_FUN);
        var name = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        var params = parseParams(tokens);
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
        tokens.skip(TokenType.SYMBOL_ARROW);
        var returnType = parseTypeLevelExpression(tokens);
        var body = parseBlock(tokens);

        return new UntypedFunctionNode(name, params, returnType, body, source);
    }

    private UntypedParamsNode parseParams(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        var positional = new ArrayList<UntypedParamNode>();
        var named = new ArrayList<UntypedParamNode>();

        parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_PAREN_CLOSE),
            () -> {
                var paramSource = source(tokens);
                if (tokens.trySkip(TokenType.SYMBOL_DOT)) {
                    named.add(parseParam(tokens, paramSource));
                } else if (named.isEmpty()) {
                    positional.add(parseParam(tokens, paramSource));
                } else {
                    throw new PositionalParamAfterNamedParamError(paramSource);
                }
                return null;
            },
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );

        return new UntypedParamsNode(positional, named, source);
    }

    public UntypedFunctionStatementNode parseFunctionStatement(TokenIterator<TokenType> tokens) {
        if (tokens.isNext(TokenType.BLANK_LINE)) {
            return parseBlankLine(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_IF)) {
            return parseIfStatement(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_FOR)) {
            return parseForEach(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_RETURN)) {
            return parseReturn(tokens);
        } else if (tokens.isNext(TokenType.COMMENT_SINGLE_LINE)) {
            return parseSingleLineComment(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_SWITCH)) {
            return parseSwitch(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_VAR)) {
            return parseVar(tokens);
        } else {
            return parseExpressionStatement(tokens);
        }
    }

    private UntypedFunctionStatementNode parseIfStatement(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        var conditionalBranches = new ArrayList<UntypedConditionalBranchNode>();

        while (true) {
            var conditionalBranch = parseConditionalBranch(tokens);
            conditionalBranches.add(conditionalBranch);

            if (!tokens.trySkip(TokenType.KEYWORD_ELSE)) {
                return new UntypedIfStatementNode(
                    conditionalBranches,
                    List.of(),
                    source
                );
            } else if (!tokens.isNext(TokenType.KEYWORD_IF)) {
                var elseBody = parseBlock(tokens);

                return new UntypedIfStatementNode(
                    conditionalBranches,
                    elseBody,
                    source
                );
            }
        }
    }

    private UntypedConditionalBranchNode parseConditionalBranch(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        tokens.skip(TokenType.KEYWORD_IF);
        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        var condition = parseTopLevelExpression(tokens);
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
        var body = parseBlock(tokens);
        return new UntypedConditionalBranchNode(condition, body, source);
    }

    private UntypedParamNode parseParam(TokenIterator<TokenType> tokens, Source source) {
        var name = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_COLON);
        var type = parseTypeLevelExpression(tokens);

        return new UntypedParamNode(name, type, source);
    }

    private UntypedNamespaceStatementNode parseInterface(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        var isSealed = tokens.trySkip(TokenType.KEYWORD_SEALED);
        tokens.skip(TokenType.KEYWORD_INTERFACE);

        var name = tokens.nextValue(TokenType.IDENTIFIER);

        tokens.skip(TokenType.SYMBOL_BRACE_OPEN);
        tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);

        return new UntypedInterfaceNode(name, isSealed, source);
    }

    public UntypedNamespaceStatementNode parseNamespaceStatement(TokenIterator<TokenType> tokens) {
        if (tokens.isNext(TokenType.BLANK_LINE)) {
            return parseBlankLine(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_ENUM)) {
            return parseEnum(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_FUN)) {
            return parseFunction(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_INTERFACE)) {
            return parseInterface(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_SEALED)) {
            return parseInterface(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_RECORD)) {
            return parseRecord(tokens);
        } else if (tokens.isNext(TokenType.COMMENT_SINGLE_LINE)) {
            return parseSingleLineComment(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_TEST)) {
            return parseTest(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_TEST_SUITE)) {
            return parseTestSuite(tokens);
        } else {
            throw new UnexpectedTokenException("namespace statement", tokens.peek().describe(), source(tokens));
        }
    }

    private UntypedRecordNode parseRecord(TokenIterator<TokenType> tokens) {
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

        List<UntypedTypeLevelExpressionNode> supertypes;
        if (tokens.trySkip(TokenType.SYMBOL_SUBTYPE)) {
            // TODO: at the time of writing, the only instance of parseOneOrMore,
            // meaning inconsistent parsing rules. Make consistent with other repeated terms?
            supertypes = parseOneOrMore(
                () -> parseTypeLevelExpression(tokens),
                () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
            );
        } else {
            supertypes = List.of();
        }

        List<UntypedRecordBodyDeclarationNode> body;
        if (tokens.trySkip(TokenType.SYMBOL_BRACE_OPEN)) {
            body = parseMany(
                () -> tokens.isNext(TokenType.SYMBOL_BRACE_CLOSE),
                () -> parseRecordBodyDeclaration(tokens),
                () -> true
            );

            tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);
        } else {
            body = List.of();
        }

        return new UntypedRecordNode(name, fieldNodes, supertypes, body, recordSource);
    }

    public UntypedRecordBodyDeclarationNode parseRecordBodyDeclaration(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        if (tokens.isNext(TokenType.BLANK_LINE)) {
            return parseBlankLine(tokens);
        } else if (tokens.isNext(TokenType.COMMENT_SINGLE_LINE)) {
            return parseSingleLineComment(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_FUN)) {
            return parseFunction(tokens);
        } else if (tokens.trySkip(TokenType.KEYWORD_PROPERTY)) {
            var name = tokens.nextValue(TokenType.IDENTIFIER);
            tokens.skip(TokenType.SYMBOL_COLON);
            var type = parseTypeLevelExpression(tokens);
            var body = parseBlock(tokens);
            return new UntypedPropertyNode(name, type, body, source);
        } else {
            // TODO: test this
            throw new UnexpectedTokenException("record body declaration", tokens.peek().describe(), source);
        }
    }

    private UntypedRecordFieldNode parseRecordField(TokenIterator<TokenType> tokens) {
        var fieldSource = source(tokens);

        var fieldName = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_COLON);
        var fieldType = parseTypeLevelExpression(tokens);

        return new UntypedRecordFieldNode(fieldName, fieldType, fieldSource);
    }

    private UntypedReferenceNode parseReference(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        return new UntypedReferenceNode(tokens.nextValue(TokenType.IDENTIFIER), source);
    }

    private UntypedReturnNode parseReturn(TokenIterator<TokenType> tokens) {
        var source = tokens.peek().source();

        tokens.skip(TokenType.KEYWORD_RETURN);
        var expression = parseTopLevelExpression(tokens);
        tokens.skip(TokenType.SYMBOL_SEMICOLON);

        return new UntypedReturnNode(expression, source);
    }

    private UntypedSingleLineCommentNode parseSingleLineComment(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        var line = tokens.nextValue(TokenType.COMMENT_SINGLE_LINE);
        var value = line.substring(2);

        return new UntypedSingleLineCommentNode(value, source);
    }

    private UntypedFunctionStatementNode parseSwitch(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_SWITCH);
        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        var expression = parseReference(tokens);
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
        tokens.skip(TokenType.SYMBOL_BRACE_OPEN);

        skipBlankLines(tokens);

        var cases = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_BRACE_CLOSE),
            () -> parseSwitchCase(tokens),
            () -> {
                skipBlankLines(tokens);
                return true;
            }
        );
        tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);

        return new UntypedSwitchNode(expression, cases, source);
    }

    private UntypedSwitchCaseNode parseSwitchCase(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_CASE);
        var type = parseTypeLevelExpression(tokens);
        var body = parseBlock(tokens);

        return new UntypedSwitchCaseNode(type, body, source);
    }

    private UntypedTestNode parseTest(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_TEST);
        var name = parseStringLiteral(tokens).value();
        var body = parseBlock(tokens);

        return new UntypedTestNode(name, body, source);
    }

    private UntypedTestSuiteNode parseTestSuite(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_TEST_SUITE);

        var name = parseStringLiteral(tokens).value();

        tokens.skip(TokenType.SYMBOL_BRACE_OPEN);
        var body = parseRepeated(
            () -> tokens.isNext(TokenType.SYMBOL_BRACE_CLOSE),
            () -> parseNamespaceStatement(tokens)
        );
        tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);

        return new UntypedTestSuiteNode(name, body, source);
    }

    public UntypedTypeLevelExpressionNode parseTypeLevelExpression(TokenIterator<TokenType> tokens) {
        var leftSource = source(tokens);
        var leftIdentifier = tokens.nextValue(TokenType.IDENTIFIER);
        var left = new UntypedTypeLevelReferenceNode(leftIdentifier, leftSource);

        if (tokens.trySkip(TokenType.SYMBOL_SQUARE_OPEN)) {
            var args = parseMany(
                () -> tokens.isNext(TokenType.SYMBOL_SQUARE_CLOSE),
                () -> parseTypeLevelExpression(tokens),
                () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
            );

            tokens.skip(TokenType.SYMBOL_SQUARE_CLOSE);
            return new UntypedConstructedTypeNode(left, args, leftSource);
        } else {
            return left;
        }
    }

    private UntypedFunctionStatementNode parseVar(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_VAR);
        var name = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_EQUALS);
        var expression = parseTopLevelExpression(tokens);
        tokens.skip(TokenType.SYMBOL_SEMICOLON);

        return new UntypedVarNode(name, expression, source);
    }

    private void skipBlankLines(TokenIterator<TokenType> tokens) {
        while (tokens.trySkip(TokenType.BLANK_LINE)) {
        }
    }

    private <T> List<T> parseRepeated(BooleanSupplier stop, Supplier<T> parseElement) {
        return parseMany(stop, parseElement, () -> true);
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

    private <T> List<T> parseOneOrMore(Supplier<T> parseElement, BooleanSupplier parseSeparator) {
        var values = new ArrayList<T>();

        while (true) {
            var element = parseElement.get();
            values.add(element);
            if (!parseSeparator.getAsBoolean()) {
                return values;
            }
        }
    }

    private Source source(TokenIterator<TokenType> tokens) {
        return tokens.peek().source();
    }
}
