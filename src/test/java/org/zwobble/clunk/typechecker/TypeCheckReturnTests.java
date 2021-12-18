package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckReturnTests {
    @Test
    public void expressionIsTypeChecked() {
        var untypedNode = Untyped.returnStatement(Untyped.boolFalse());

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode);

        assertThat(result, isTypedReturnNode(
            typedReturnNodeHasExpression(isTypedBoolLiteral(false))
        ));
    }
}
