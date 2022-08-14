package org.zwobble.clunk.parser;

import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.tokeniser.TokenIterator;
import org.zwobble.clunk.tokeniser.UnexpectedTokenException;
import org.zwobble.clunk.types.NamespaceName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Parser {
    private final Source fullSource;

    public Parser(Source fullSource) {
        this.fullSource = fullSource;
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

    public UntypedExpressionNode parseExpression(TokenIterator<TokenType> tokens) {
        var expression = parsePrimaryExpression(tokens);

        // TODO: handle precedence properly
        while (true) {
            if (tokens.trySkip(TokenType.SYMBOL_DOT)) {
                var fieldName = tokens.nextValue(TokenType.IDENTIFIER);
                expression = new UntypedMemberAccessNode(expression, fieldName, expression.source());
            } else if (tokens.trySkip(TokenType.SYMBOL_PAREN_OPEN)) {
                var positionalArgs = parseMany(
                    () -> tokens.isNext(TokenType.SYMBOL_PAREN_CLOSE),
                    () -> parseExpression(tokens),
                    () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
                );
                tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
                expression = new UntypedCallNode(expression, positionalArgs, expression.source());
            } else if (tokens.trySkip(TokenType.SYMBOL_PLUS)) {
                var right = parsePrimaryExpression(tokens);
                expression = new UntypedAddNode(expression, right, expression.source());
            } else if (tokens.trySkip(TokenType.SYMBOL_SQUARE_OPEN)) {
                var index = parseExpression(tokens);
                tokens.skip(TokenType.SYMBOL_SQUARE_CLOSE);
                expression = new UntypedIndexNode(expression, index, expression.source());
            } else {
                return expression;
            }
        }
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
            var expression = parseExpression(tokens);
            tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
            return expression;
        } else {
            throw new UnexpectedTokenException("primary expression", tokens.peek().describe(), source);
        }
    }

    private UntypedExpressionStatementNode parseExpressionStatement(TokenIterator<TokenType> tokens) {
        var source = source(tokens);
        var expression = parseExpression(tokens);
        tokens.skip(TokenType.SYMBOL_SEMICOLON);
        return new UntypedExpressionStatementNode(expression, source);
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

    public UntypedNamespaceNode parseNamespace(TokenIterator<TokenType> tokens, NamespaceName name) {
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

        return new UntypedNamespaceNode(name, imports, statements, source);
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

    private UntypedNamespaceStatementNode parseFunction(TokenIterator<TokenType> tokens) {
        var source = tokens.peek().source();

        tokens.skip(TokenType.KEYWORD_FUN);
        var name = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_PAREN_OPEN);
        var params = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_PAREN_CLOSE),
            () -> parseParam(tokens),
            () -> tokens.trySkip(TokenType.SYMBOL_COMMA)
        );
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
        tokens.skip(TokenType.SYMBOL_ARROW);
        var returnType = parseTypeLevelExpression(tokens);
        var body = parseBlock(tokens);

        return new UntypedFunctionNode(name, params, returnType, body, source);
    }

    public UntypedFunctionStatementNode parseFunctionStatement(TokenIterator<TokenType> tokens) {
        if (tokens.isNext(TokenType.BLANK_LINE)) {
            return parseBlankLine(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_IF)) {
            return parseIfStatement(tokens);
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
        var condition = parseExpression(tokens);
        tokens.skip(TokenType.SYMBOL_PAREN_CLOSE);
        var body = parseBlock(tokens);
        return new UntypedConditionalBranchNode(condition, body, source);
    }

    private UntypedParamNode parseParam(TokenIterator<TokenType> tokens) {
        var source = tokens.peek().source();

        var name = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_COLON);
        var type = parseTypeLevelExpression(tokens);

        return new UntypedParamNode(name, type, source);
    }

    private UntypedNamespaceStatementNode parseInterface(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_SEALED);
        tokens.skip(TokenType.KEYWORD_INTERFACE);

        var name = tokens.nextValue(TokenType.IDENTIFIER);

        tokens.skip(TokenType.SYMBOL_BRACE_OPEN);
        tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);

        return new UntypedInterfaceNode(name, source);
    }

    public UntypedNamespaceStatementNode parseNamespaceStatement(TokenIterator<TokenType> tokens) {
        if (tokens.isNext(TokenType.BLANK_LINE)) {
            return parseBlankLine(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_ENUM)) {
            return parseEnum(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_FUN)) {
            return parseFunction(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_SEALED)) {
            return parseInterface(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_RECORD)) {
            return parseRecord(tokens);
        } else if (tokens.isNext(TokenType.COMMENT_SINGLE_LINE)) {
            return parseSingleLineComment(tokens);
        } else if (tokens.isNext(TokenType.KEYWORD_TEST)) {
            return parseTest(tokens);
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
            supertypes = List.of(parseTypeLevelExpression(tokens));
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

    private UntypedRecordBodyDeclarationNode parseRecordBodyDeclaration(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_PROPERTY);
        var name = tokens.nextValue(TokenType.IDENTIFIER);
        tokens.skip(TokenType.SYMBOL_COLON);
        var type = parseTypeLevelExpression(tokens);
        var body = parseBlock(tokens);

        return new UntypedPropertyNode(name, type, body, source);
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
        var expression = parseExpression(tokens);
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
        var cases = parseMany(
            () -> tokens.isNext(TokenType.SYMBOL_BRACE_CLOSE),
            () -> parseSwitchCase(tokens),
            () -> true
        );
        tokens.skip(TokenType.SYMBOL_BRACE_CLOSE);

        return new UntypedSwitchNode(expression, cases, source);
    }

    private UntypedSwitchCaseNode parseSwitchCase(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_CASE);
        var type = parseTypeLevelExpression(tokens);
        var variableName = tokens.nextValue(TokenType.IDENTIFIER);
        var body = parseBlock(tokens);

        return new UntypedSwitchCaseNode(type, variableName, body, source);
    }

    private UntypedNamespaceStatementNode parseTest(TokenIterator<TokenType> tokens) {
        var source = source(tokens);

        tokens.skip(TokenType.KEYWORD_TEST);
        var name = parseStringLiteral(tokens).value();
        var body = parseBlock(tokens);

        return new UntypedTestNode(name, body, source);
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
        var expression = parseExpression(tokens);
        tokens.skip(TokenType.SYMBOL_SEMICOLON);

        return new UntypedVarNode(name, expression, source);
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

    private Source source(TokenIterator<TokenType> tokens) {
        return tokens.peek().source();
    }
}
