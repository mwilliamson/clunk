package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckFunctionTests {
    @Test
    public void typedFunctionHasSameNameAsUntypedFunction() {
        var untypedNode = UntypedFunctionNode.builder()
            .name("f")
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(untypedNode);

        assertThat(result, isTypedFunctionNode().withName("f"));
    }

    @Test
    public void paramsAreTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .addParam(Untyped.param("x", Untyped.staticReference("Int")))
            .addParam(Untyped.param("y", Untyped.staticReference("String")))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(untypedNode);

        assertThat(result, isTypedFunctionNode().withParams(contains(
            isTypedParamNode().withName("x").withType(IntType.INSTANCE),
            isTypedParamNode().withName("y").withType(StringType.INSTANCE)
        )));
    }

    @Test
    public void returnTypeIsTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.staticReference("Int"))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(untypedNode);

        assertThat(result, isTypedFunctionNode().withReturnType(isTypedStaticExpressionNode(IntType.INSTANCE)));
    }

    @Test
    public void bodyIsTypeChecked() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.staticReference("Bool"))
            .addBodyStatement(Untyped.returnStatement(Untyped.boolFalse()))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(untypedNode);

        assertThat(result, isTypedFunctionNode().withBody(contains(
            isTypedReturnNode(
                typedReturnNodeHasExpression(isTypedBoolLiteral(false))
            )
        )));
    }
}
