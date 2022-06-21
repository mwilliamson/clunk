package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIfStatementNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void whenConditionIsNotBoolThenErrorIsThrown() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.intLiteral(42), List.of())
            ),
            List.of()
        );

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub())
        );

        assertThat(result.getExpected(), equalTo(Types.BOOL));
        assertThat(result.getActual(), equalTo(Types.INT));
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

    @Test
    public void elseBodyIsTypeChecked() {
        var untypedNode = Untyped.ifStatement(
            List.of(),
            List.of(
                Untyped.expressionStatement(Untyped.intLiteral(42))
            )
        );

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), allOf(
            isA(TypedIfStatementNode.class),
            has("elseBody", contains(isTypedExpressionStatement(isTypedIntLiteralNode(42))))
        ));
    }
}