package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;
import static org.zwobble.clunk.matchers.MapEntryMatcher.isMapEntry;
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
    public void positionalParamsAreTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .addPositionalParam(Untyped.param("x", Untyped.typeLevelReference("Int")))
            .addPositionalParam(Untyped.param("y", Untyped.typeLevelReference("String")))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withPositionalParams(isSequence(
            isTypedParamNode().withName("x").withType(IntType.INSTANCE),
            isTypedParamNode().withName("y").withType(StringType.INSTANCE)
        )));
    }

    @Test
    public void namedParamsAreTyped() {
        var untypedNode = UntypedFunctionNode.builder()
            .addNamedParam(Untyped.param("x", Untyped.typeLevelReference("Int")))
            .addNamedParam(Untyped.param("y", Untyped.typeLevelReference("String")))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withNamedParams(isSequence(
            isTypedParamNode().withName("x").withType(IntType.INSTANCE),
            isTypedParamNode().withName("y").withType(StringType.INSTANCE)
        )));
    }

    @Test
    public void whenNamedParamsAreNotInLexicographicalOrderThenErrorIsThrown() {
        var untypedNode = UntypedFunctionNode.builder()
            .addNamedParam(Untyped.param("y", Untyped.typeLevelReference("String")))
            .addNamedParam(Untyped.param("x", Untyped.typeLevelReference("Int")))
            .build();

        assertThrows(
            NamedParamsNotInLexicographicalOrderError.class,
            () -> typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub())
        );
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

        assertThat(result.typedNode(), isTypedFunctionNode().withBody(isSequence(
            isTypedReturnNode().withExpression(isTypedBoolLiteralNode(false))
        )));
    }

    @Test
    public void bodyCanAccessPositionalParams() {
        var untypedNode = UntypedFunctionNode.builder()
            .addPositionalParam(Untyped.param("x", Untyped.typeLevelReference("Bool")))
            .returnType(Untyped.typeLevelReference("Bool"))
            .addBodyStatement(Untyped.returnStatement(Untyped.reference("x")))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withBody(isSequence(
            isTypedReturnNode().withExpression(isTypedReferenceNode().withName("x").withType(Types.BOOL))
        )));
    }

    @Test
    public void bodyCanAccessNamedParams() {
        var untypedNode = UntypedFunctionNode.builder()
            .addNamedParam(Untyped.param("x", Untyped.typeLevelReference("Bool")))
            .returnType(Untyped.typeLevelReference("Bool"))
            .addBodyStatement(Untyped.returnStatement(Untyped.reference("x")))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), isTypedFunctionNode().withBody(isSequence(
            isTypedReturnNode().withExpression(isTypedReferenceNode().withName("x").withType(Types.BOOL))
        )));
    }

    @Test
    public void givenFunctionHasUnitReturnTypeWhenBodyDoesNotReturnThenFunctionTypeChecks() {
        var untypedNode = UntypedFunctionNode.builder()
            .returnType(Untyped.typeLevelReference("Unit"))
            .build();

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, TypeCheckerContext.stub());

        var typedNode = (TypedFunctionNode) result.typedNode();
        assertThat(typedNode.body(), isSequence());
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
            .addPositionalParam(Untyped.param("x", Untyped.typeLevelReference("Int")))
            .addNamedParam(Untyped.param("y", Untyped.typeLevelReference("Bool")))
            .returnType(Untyped.typeLevelReference("String"))
            .addBodyStatement(Untyped.returnStatement(Untyped.string()))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.context().typeOf("f", NullSource.INSTANCE),
            equalTo(Types.staticFunctionType(
                NamespaceId.source("a", "b"),
                "f",
                List.of(Types.INT),
                List.of(Types.namedParam("y", Types.BOOL)),
                Types.STRING,
                Visibility.PUBLIC
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

    @Test
    public void addsNamespaceField() {
        var untypedNode = UntypedFunctionNode.builder()
            .name("f")
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.fieldType(),
            isOptionalOf(isMapEntry(
                equalTo("f"),
                instanceOf(
                    StaticFunctionType.class,
                    has("functionName", x -> x.functionName(), equalTo("f"))
                )
            ))
        );
    }
}
