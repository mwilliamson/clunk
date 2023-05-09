package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedComprehensionForClauseNode;
import org.zwobble.clunk.ast.typed.TypedComprehensionIfClauseNode;
import org.zwobble.clunk.ast.typed.TypedExpressionNode;
import org.zwobble.clunk.ast.typed.TypedListComprehensionNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;
import org.zwobble.precisely.Matcher;

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
            has("forClauses", x -> x.forClauses(), isSequence(
                allOf(
                    hasTargetName(equalTo("x")),
                    hasIterable(isTypedReferenceNode().withName("xs")),
                    hasIfClauses(isSequence())
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
            has("forClauses", x -> x.forClauses(), isSequence(
                allOf(
                    hasTargetName(equalTo("xs")),
                    hasIterable(isTypedReferenceNode().withName("xss")),
                    hasIfClauses(isSequence())
                ),
                allOf(
                    hasTargetName(equalTo("x")),
                    hasIterable(isTypedReferenceNode().withName("xs")),
                    hasIfClauses(isSequence())
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

    @Test
    public void ifClausesAreTyped() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable(
                    "x",
                    Untyped.reference("xs"),
                    List.of(
                        Untyped.reference("y")
                    )
                )
            ),
            Untyped.string()
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.STRING), NullSource.INSTANCE)
            .addLocal("y", Types.BOOL, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListComprehensionNode.class,
            has("forClauses", x -> x.forClauses(), isSequence(
                hasIfClauses(isSequence(
                    hasCondition(isTypedReferenceNode().withName("y").withType(Types.BOOL))
                ))
            ))
        ));
    }

    @Test
    public void whenConditionIsNotBoolThenErrorIsThrown() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable(
                    "x",
                    Untyped.reference("xs"),
                    List.of(
                        Untyped.intLiteral()
                    )
                )
            ),
            Untyped.string()
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.STRING), NullSource.INSTANCE)
            .addLocal("y", Types.BOOL, NullSource.INSTANCE);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getActual(), equalTo(Types.INT));
        assertThat(result.getExpected(), equalTo(Types.BOOL));
    }

    @Test
    public void targetOfIterableClauseIsAvailableInLaterIfClauses() {
        var untypedNode = Untyped.listComprehension(
            List.of(
                Untyped.comprehensionIterable(
                    "x",
                    Untyped.reference("xs"),
                    List.of(
                        Untyped.reference("x")
                    )
                )
            ),
            Untyped.string()
        );
        var context = TypeCheckerContext.stub()
            .addLocal("xs", Types.list(Types.BOOL), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedListComprehensionNode.class,
            has("forClauses", x -> x.forClauses(), isSequence(
                hasIfClauses(isSequence(
                    hasCondition(isTypedReferenceNode().withName("x").withType(Types.BOOL))
                ))
            ))
        ));
    }

    private static Matcher<TypedComprehensionForClauseNode> hasTargetName(Matcher<String> targetName) {
        return has("targetName", x -> x.targetName(), targetName);
    }

    private static Matcher<TypedComprehensionForClauseNode> hasIterable(Matcher<? super TypedExpressionNode> iterable) {
        return has("iterable", x -> x.iterable(), iterable);
    }

    private static Matcher<TypedComprehensionForClauseNode> hasIfClauses(Matcher<Iterable<TypedComprehensionIfClauseNode>> ifClauses) {
        return has("ifClauses", x -> x.ifClauses(), ifClauses);
    }

    private static Matcher<TypedComprehensionIfClauseNode> hasCondition(Matcher<? super TypedExpressionNode> condition) {
        return has("condition", x -> x.condition(), condition);
    }
}
