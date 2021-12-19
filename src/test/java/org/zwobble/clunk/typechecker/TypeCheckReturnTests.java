package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckReturnTests {
    @Test
    public void expressionIsTypeChecked() {
        var untypedNode = Untyped.returnStatement(Untyped.boolFalse());
        var context = new TypeCheckerFunctionContext(BoolType.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result, isTypedReturnNode(
            typedReturnNodeHasExpression(isTypedBoolLiteral(false))
        ));
    }

    @Test
    public void whenTypeOfExpressionIsNotSubtypeOfFunctionReturnTypeThenErrorIsThrown() {
        var untypedNode = Untyped.returnStatement(Untyped.boolFalse());
        var context = new TypeCheckerFunctionContext(StringType.INSTANCE);

        var error = Assertions.assertThrows(UnexpectedTypeError.class, () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context));

        assertThat(error.getExpected(), equalTo(StringType.INSTANCE));
        assertThat(error.getActual(), equalTo(BoolType.INSTANCE));
    }
}