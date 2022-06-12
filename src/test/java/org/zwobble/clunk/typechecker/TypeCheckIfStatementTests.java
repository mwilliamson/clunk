package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIfStatementNode;
import org.zwobble.clunk.ast.untyped.Untyped;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckIfStatementTests {
    @Test
    public void conditionIsTypeChecked() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.boolFalse(), List.of())
            ),
            List.of()
        );

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), allOf(
            isA(TypedIfStatementNode.class),
            has("conditionalBranches", contains(
                has("condition", isTypedBoolLiteralNode(false))
            ))
        ));
    }

    @Test
    public void conditionalBodyIsTypeChecked() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.boolFalse(), List.of(
                    Untyped.expressionStatement(Untyped.intLiteral(42))
                ))
            ),
            List.of()
        );

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), allOf(
            isA(TypedIfStatementNode.class),
            has("conditionalBranches", contains(
                has("body", contains(isTypedExpressionStatement(isTypedIntLiteralNode(42))))
            ))
        ));
    }
}
