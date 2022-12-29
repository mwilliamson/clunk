package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedForEachNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckForEachTests {
    @Test
    public void childNodesAreTypedChecked() {
        var untypedNode = Untyped.forEach(
            "x",
            Untyped.reference("xs"),
            List.of(
                Untyped.expressionStatement(Untyped.string("hello"))
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.INT), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), contains(
            allOf(
                isA(TypedForEachNode.class),
                has("targetName", equalTo("x")),
                has("targetType", equalTo(Types.INT)),
                has("iterable", isTypedReferenceNode().withName("xs").withType(Types.list(Types.INT))),
                has("body", contains(
                    isTypedExpressionStatementNode(isTypedStringLiteralNode("hello"))
                ))
            )
        ));
    }

    @Test
    public void targetVariableIsAvailableInBody() {
        var untypedNode = Untyped.forEach(
            "x",
            Untyped.reference("xs"),
            List.of(
                Untyped.expressionStatement(Untyped.reference("x"))
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.INT), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), contains(
            allOf(
                isA(TypedForEachNode.class),
                has("body", contains(
                    isTypedExpressionStatementNode(isTypedReferenceNode().withName("x").withType(Types.INT))
                ))
            )
        ));
        assertThat(context.currentFrame().environment().containsKey("x"), equalTo(false));
    }

    @Test
    public void whenIterableIsNotListThenErrorIsThrown() {
        var untypedNode = Untyped.forEach(
            "x",
            Untyped.reference("xs"),
            List.of(
                Untyped.expressionStatement(Untyped.string("hello"))
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.INT, NullSource.INSTANCE);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context)
        );

        assertThat(result.getActual(), equalTo(Types.INT));
        assertThat(result.getExpected(), equalTo(Types.LIST_CONSTRUCTOR.genericType()));
    }

    @Test
    public void whenBodyHasNoReturnsThenForLoopNeverReturns() {
        var untypedNode = Untyped.forEach(
            "x",
            Untyped.reference("xs"),
            List.of(
                Untyped.expressionStatement(Untyped.string("hello"))
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.INT), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returnBehaviour(), equalTo(ReturnBehaviour.NEVER));
    }

    @Test
    public void whenBodyHasReturnThenForLoopSometimesReturns() {
        var untypedNode = Untyped.forEach(
            "x",
            Untyped.reference("xs"),
            List.of(
                Untyped.returnStatement(Untyped.string("hello"))
            )
        );
        var context = TypeCheckerContext.stub()
            .enterFunction(Types.STRING)
            .addLocal("xs", Types.list(Types.INT), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returnBehaviour(), equalTo(ReturnBehaviour.SOMETIMES));
    }
}
