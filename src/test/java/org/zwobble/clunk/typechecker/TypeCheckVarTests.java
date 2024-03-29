package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.BoolType;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedBoolLiteralNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedVarNode;

public class TypeCheckVarTests {
    @Test
    public void expressionIsTypeChecked() {
        var untypedNode = Untyped.var("x", Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), isSequence(
            isTypedVarNode().withName("x").withExpression(isTypedBoolLiteralNode(false))
        ));
    }

    @Test
    public void nameHasInferredTypeInEnvironment() {
        var untypedNode = Untyped.var("x", Untyped.boolFalse());
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.context().typeOf("x", NullSource.INSTANCE), equalTo(BoolType.INSTANCE));
    }
}
