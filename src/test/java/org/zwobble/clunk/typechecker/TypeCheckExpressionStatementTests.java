package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedBoolLiteralNode;
import org.zwobble.clunk.ast.typed.TypedExpressionStatementNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.zwobble.precisely.Matchers.has;

public class TypeCheckExpressionStatementTests {
    @Test
    public void expressionIsTypeChecked() {
        var untypedNode = Untyped.expressionStatement(Untyped.boolFalse());

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedExpressionStatementNode.class,
                has("expression", x -> x.expression(), instanceOf(
                    TypedBoolLiteralNode.class,
                    has("value", x -> x.value(), equalTo(false))
                ))
            )
        ));
    }
}
