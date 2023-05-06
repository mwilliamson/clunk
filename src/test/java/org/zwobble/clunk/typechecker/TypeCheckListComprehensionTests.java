package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedListComprehensionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckListComprehensionTests {
    @Test
    public void typeOfListComprehensionIsListOfYieldExpressionType() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable("x", Untyped.reference("xs"))
            ),
            Untyped.string()
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.STRING), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, has("type", x -> x.type(), equalTo(Types.list(Types.STRING))));
    }

    @Test
    public void iterableClausesAreTyped() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable("x", Untyped.reference("xs"))
            ),
            Untyped.string()
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.STRING), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListComprehensionNode.class,
            has("iterables", x -> x.iterables(), isSequence(
                allOf(
                    has("targetName", x -> x.targetName(), equalTo("x")),
                    has("iterable", x -> x.iterable(), isTypedReferenceNode().withName("xs")),
                    has("conditions", x -> x.conditions(), isSequence())
                )
            ))
        ));
    }

    @Test
    public void targetOfIterableClauseIsAvailableInYieldClause() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable("x", Untyped.reference("xs"))
            ),
            Untyped.reference("x")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.STRING), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListComprehensionNode.class,
            has("yield", x -> x.yield(), isTypedReferenceNode().withName("x").withType(Types.STRING))
        ));
    }

    @Test
    public void targetOfIterableClauseIsAvailableInLaterIterableClauses() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable("xs", Untyped.reference("xss")),
                Untyped.comprehensionIterable("x", Untyped.reference("xs"))
            ),
            Untyped.reference("x")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xss", Types.list(Types.list(Types.STRING)), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListComprehensionNode.class,
            has("iterables", x -> x.iterables(), isSequence(
                allOf(
                    has("targetName", x -> x.targetName(), equalTo("xs")),
                    has("iterable", x -> x.iterable(), isTypedReferenceNode().withName("xss")),
                    has("conditions", x -> x.conditions(), isSequence())
                ),
                allOf(
                    has("targetName", x -> x.targetName(), equalTo("x")),
                    has("iterable", x -> x.iterable(), isTypedReferenceNode().withName("xs")),
                    has("conditions", x -> x.conditions(), isSequence())
                )
            )),
            has("yield", x -> x.yield(), isTypedReferenceNode().withName("x").withType(Types.STRING)),
            has("type", x -> x.type(), equalTo(Types.list(Types.STRING)))
        ));
    }

    @Test
    public void whenIterableExpressionIsNotIterableThenErrorIsThrown() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable("x", Untyped.reference("xs"))
            ),
            Untyped.reference("x")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.INT, NullSource.INSTANCE);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getActual(), equalTo(Types.INT));
        assertThat(result.getExpected(), equalTo(Types.list(Types.OBJECT)));
    }
}
