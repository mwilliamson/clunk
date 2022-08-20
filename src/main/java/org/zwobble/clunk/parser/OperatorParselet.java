package org.zwobble.clunk.parser;

import org.zwobble.clunk.ast.untyped.UntypedExpressionNode;
import org.zwobble.clunk.tokeniser.TokenIterator;

public interface OperatorParselet {
    OperatorPrecedence precedence();
    UntypedExpressionNode parse(UntypedExpressionNode left, TokenIterator<TokenType> tokens);
    TokenType tokenType();
}
