package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedBoolLiteralNode;
import org.zwobble.clunk.ast.typed.TypedExpressionStatementNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckExpressionStatementTests {
    @Test
    public void expressionIsTypeChecked() {
        var untypedNode = Untyped.expressionStatement(Untyped.boolFalse());

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerFunctionContext.stub());

        assertThat(result.typedNode(), allOf(
            isA(TypedExpressionStatementNode.class),
            has("expression", allOf(
                isA(TypedBoolLiteralNode.class),
                has("value", equalTo(false))
            ))
        ));
    }
}
