package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.StringType;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckReturnTests {
    @Test
    public void expressionIsTypeChecked() {
        var untypedNode = Untyped.returnStatement(Untyped.boolFalse());
        var context = TypeCheckerContext.stub().enterFunction(BoolType.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), isSequence(
            isTypedReturnNode().withExpression(isTypedBoolLiteralNode(false))
        ));
    }

    @Test
    public void whenTypeOfExpressionIsNotSubtypeOfFunctionReturnTypeThenErrorIsThrown() {
        var untypedNode = Untyped.returnStatement(Untyped.boolFalse());
        var context = TypeCheckerContext.stub().enterFunction(StringType.INSTANCE);

        var error = Assertions.assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context));

        assertThat(error.getExpected(), equalTo(StringType.INSTANCE));
        assertThat(error.getActual(), equalTo(BoolType.INSTANCE));
    }

    @Test
    public void whenInContextWithoutReturnThenErrorIsThrown() {
        var untypedNode = Untyped.returnStatement();
        var context = TypeCheckerContext.stub().enterTest();

        Assertions.assertThrows(CannotReturnHereError.class, () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context));
    }
}
