package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckFunctionTests {
    @Test
    public void typedFunctionHasSameNameAsUntypedFunction() {
        var untypedNode = UntypedFunctionNode.builder()
            .name("f")
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeChecker.defineVariablesForNamespaceStatement(untypedNode, TypeCheckerContext.stub())
        );

        assertThat(result.typedNode(), isTypedFunctionNode().withName("f"));
    }

    @Test
    public void paramsAreTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .addParam(Untyped.param("x", Untyped.staticReference("Int")))
            .addParam(Untyped.param("y", Untyped.staticReference("String")))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeChecker.defineVariablesForNamespaceStatement(untypedNode, TypeCheckerContext.stub())
        );

        assertThat(result.typedNode(), isTypedFunctionNode().withParams(contains(
            isTypedParamNode().withName("x").withType(IntType.INSTANCE),
            isTypedParamNode().withName("y").withType(StringType.INSTANCE)
        )));
    }

    @Test
    public void returnTypeIsTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.staticReference("Int"))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeChecker.defineVariablesForNamespaceStatement(untypedNode, TypeCheckerContext.stub())
        );

        assertThat(result.typedNode(), isTypedFunctionNode().withReturnType(IntType.INSTANCE));
    }

    @Test
    public void bodyIsTypeChecked() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.staticReference("Bool"))
            .addBodyStatement(Untyped.returnStatement(Untyped.boolFalse()))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeChecker.defineVariablesForNamespaceStatement(untypedNode, TypeCheckerContext.stub())
        );

        assertThat(result.typedNode(), isTypedFunctionNode().withBody(contains(
            isTypedReturnNode().withExpression(isTypedBoolLiteralNode(false))
        )));
    }

    @Test
    public void functionTypeIsAddedToEnvironment() {
        var untypedNode = UntypedFunctionNode.builder()
            .name("f")
            .addParam(Untyped.param("x", Untyped.staticReference("Int")))
            .returnType(Untyped.staticReference("String"))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = TypeChecker.defineVariablesForNamespaceStatement(
            untypedNode,
            context
        );

        assertThat(
            result.typeOf("f", NullSource.INSTANCE),
            equalTo(new StaticFunctionType(
                NamespaceName.fromParts("a", "b"),
                "f",
                List.of(Types.INT),
                Types.STRING
            ))
        );
    }
}
