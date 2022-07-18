package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckFunctionTests {
    @Test
    public void typedFunctionHasSameNameAsUntypedFunction() {
        var untypedNode = UntypedFunctionNode.builder()
            .name("f")
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withName("f"));
    }

    @Test
    public void paramsAreTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .addParam(Untyped.param("x", Untyped.typeLevelReference("Int")))
            .addParam(Untyped.param("y", Untyped.typeLevelReference("String")))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withParams(contains(
            isTypedParamNode().withName("x").withType(IntType.INSTANCE),
            isTypedParamNode().withName("y").withType(StringType.INSTANCE)
        )));
    }

    @Test
    public void returnTypeIsTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.typeLevelReference("Int"))
            .addBodyStatement(Untyped.returnStatement(Untyped.intLiteral()))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withReturnType(IntType.INSTANCE));
    }

    @Test
    public void bodyIsTypeChecked() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.typeLevelReference("Bool"))
            .addBodyStatement(Untyped.returnStatement(Untyped.boolFalse()))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withBody(contains(
            isTypedReturnNode().withExpression(isTypedBoolLiteralNode(false))
        )));
    }

    @Test
    public void givenFunctionHasUnitReturnTypeWhenBodyDoesNotReturnThenFunctionTypeChecks() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.typeLevelReference("Unit"))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        var typedNode = (TypedFunctionNode) result.typedNode();
        assertThat(typedNode.body(), empty());
    }

    @Test
    public void givenFunctionHasNonUnitReturnTypeWhenBodyDoesNotReturnThenErrorIsThrown() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.typeLevelReference("Bool"))
            .build();

        assertThrows(MissingReturnError.class, () -> typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub()));
    }

    @Test
    public void functionTypeIsAddedToEnvironment() {
        var untypedNode = UntypedFunctionNode.builder()
            .name("f")
            .addParam(Untyped.param("x", Untyped.typeLevelReference("Int")))
            .returnType(Untyped.typeLevelReference("String"))
            .addBodyStatement(Untyped.returnStatement(Untyped.string()))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.context().typeOf("f", NullSource.INSTANCE),
            equalTo(new StaticFunctionType(
                NamespaceName.fromParts("a", "b"),
                "f",
                List.of(Types.INT),
                Types.STRING
            ))
        );
    }

    @Test
    public void returnedContextLeavesBodyEnvironment() {
        var untypedNode = UntypedFunctionNode.builder()
            .addBodyStatement(Untyped.var("x", Untyped.boolFalse()))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.context().currentFrame().environment().containsKey("x"), equalTo(false));
    }
}
