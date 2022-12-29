package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIfStatementNode;
import org.zwobble.clunk.ast.typed.TypedTypeNarrowNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckIfStatementTests {
    @Test
    public void conditionIsTypeChecked() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.boolFalse(), List.of())
            ),
            List.of()
        );

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.value(), contains(
            allOf(
                isA(TypedIfStatementNode.class),
                has("conditionalBranches", contains(
                    has("condition", isTypedBoolLiteralNode(false))
                ))
            )
        ));
    }

    @Test
    public void whenConditionIsNotBoolThenErrorIsThrown() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.intLiteral(42), List.of())
            ),
            List.of()
        );

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub())
        );

        assertThat(result.getExpected(), equalTo(Types.BOOL));
        assertThat(result.getActual(), equalTo(Types.INT));
    }

    @Test
    public void conditionalBodyIsTypeChecked() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.boolFalse(), List.of(
                    Untyped.expressionStatement(Untyped.intLiteral(42))
                ))
            ),
            List.of()
        );

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.value(), contains(
            allOf(
                isA(TypedIfStatementNode.class),
                has("conditionalBranches", contains(
                    has("body", contains(isTypedExpressionStatementNode(isTypedIntLiteralNode(42))))
                ))
            )
        ));
    }

    @Test
    public void elseBodyIsTypeChecked() {
        var untypedNode = Untyped.ifStatement(
            List.of(),
            List.of(
                Untyped.expressionStatement(Untyped.intLiteral(42))
            )
        );

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, TypeCheckerContext.stub());

        assertThat(result.value(), contains(
            allOf(
                isA(TypedIfStatementNode.class),
                has("elseBody", contains(isTypedExpressionStatementNode(isTypedIntLiteralNode(42))))
            )
        ));
    }

    @Test
    public void whenElseBranchDoesNotReturnThenIfStatementDoesNotReturn() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.boolFalse(), List.of(Untyped.returnStatement(Untyped.intLiteral())))
            ),
            List.of()
        );
        var context = TypeCheckerContext.stub().enterFunction(Types.INT);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returnBehaviour(), equalTo(ReturnBehaviour.SOMETIMES));
    }

    @Test
    public void whenAnyConditionalBranchDoesNotReturnThenIfStatementDoesNotReturn() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.boolFalse(), List.of()),
                Untyped.conditionalBranch(Untyped.boolTrue(), List.of(Untyped.returnStatement(Untyped.intLiteral())))
            ),
            List.of(Untyped.returnStatement(Untyped.intLiteral()))
        );
        var context = TypeCheckerContext.stub().enterFunction(Types.INT);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returnBehaviour(), equalTo(ReturnBehaviour.SOMETIMES));
    }

    @Test
    public void whenAllConditionalBranchesAndElseBranchReturnThenIfStatementReturns() {
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(Untyped.boolFalse(), List.of(Untyped.returnStatement(Untyped.intLiteral()))),
                Untyped.conditionalBranch(Untyped.boolTrue(), List.of(Untyped.returnStatement(Untyped.intLiteral())))
            ),
            List.of(Untyped.returnStatement(Untyped.intLiteral()))
        );
        var context = TypeCheckerContext.stub().enterFunction(Types.INT);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returnBehaviour(), equalTo(ReturnBehaviour.ALWAYS));
    }

    @Test
    public void whenConditionIsInstanceOfCheckThenConditionalBodyHasNarrowedTypeForVariable() {
        var namespaceName = NamespaceName.fromParts("example");
        var interfaceType = Types.interfaceType(namespaceName, "Interface");
        var recordType = Types.recordType(namespaceName, "Record");
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(
                    Untyped.instanceOf(
                        Untyped.reference("x"),
                        Untyped.typeLevelReference("Record")
                    ),
                    List.of(Untyped.returnStatement(Untyped.reference("x")))
                )
            ),
            List.of(Untyped.returnStatement(Untyped.reference("x")))
        );
        var context = TypeCheckerContext.stub()
            .addSealedInterfaceCase(interfaceType, recordType)
            .enterFunction(Types.OBJECT)
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("Record", Types.metaType(recordType), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), contains(
            allOf(
                isA(TypedIfStatementNode.class),
                has("conditionalBranches", contains(
                    has("body", contains(
                        isTypedTypeNarrowNode("x", equalTo(recordType)),
                        isTypedReturnNode().withExpression(isTypedReferenceNode().withType(recordType))
                    ))
                )),
                has("elseBody", contains(isTypedReturnNode().withExpression(isTypedReferenceNode().withType(interfaceType))))
            )
        ));
    }

    @Test
    public void whenConditionIsNegatedInstanceOfCheckThenFollowingBranchesHaveNarrowedTypeForVariable() {
        var namespaceName = NamespaceName.fromParts("example");
        var interfaceType = Types.interfaceType(namespaceName, "Interface");
        var recordType = Types.recordType(namespaceName, "Record");
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(
                    Untyped.boolFalse(),
                    List.of(Untyped.expressionStatement(Untyped.reference("x")))
                ),
                Untyped.conditionalBranch(
                    Untyped.logicalNot(Untyped.instanceOf(
                        Untyped.reference("x"),
                        Untyped.typeLevelReference("Record")
                    )),
                    List.of(Untyped.expressionStatement(Untyped.reference("x")))
                ),
                Untyped.conditionalBranch(
                    Untyped.boolFalse(),
                    List.of(Untyped.expressionStatement(Untyped.reference("x")))
                )
            ),
            List.of(Untyped.expressionStatement(Untyped.reference("x")))
        );
        var context = TypeCheckerContext.stub()
            .addSealedInterfaceCase(interfaceType, recordType)
            .enterFunction(Types.OBJECT)
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("Record", Types.metaType(recordType), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), contains(
            allOf(
                isA(TypedIfStatementNode.class),
                has("conditionalBranches", contains(
                    has("body", contains(isTypedExpressionStatementNode(isTypedReferenceNode().withType(interfaceType)))),
                    has("body", contains(isTypedExpressionStatementNode(isTypedReferenceNode().withType(interfaceType)))),
                    has("body", contains(
                        isTypedTypeNarrowNode("x", equalTo(recordType)),
                        isTypedExpressionStatementNode(isTypedReferenceNode().withType(recordType))
                    ))
                )),
                has("elseBody", contains(
                    isTypedTypeNarrowNode("x", equalTo(recordType)),
                    isTypedExpressionStatementNode(isTypedReferenceNode().withType(recordType))
                ))
            )
        ));
    }

    @Test
    public void whenConditionIsNegatedInstanceOfCheckWithNonReturningBodyThenFollowingStatementsHaveUnchangedTypeForVariable() {
        var namespaceName = NamespaceName.fromParts("example");
        var interfaceType = Types.interfaceType(namespaceName, "Interface");
        var recordType = Types.recordType(namespaceName, "Record");
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(
                    Untyped.logicalNot(Untyped.instanceOf(
                        Untyped.reference("x"),
                        Untyped.typeLevelReference("Record")
                    )),
                    List.of(
                        Untyped.ifStatement(
                            List.of(
                                Untyped.conditionalBranch(
                                    Untyped.reference("maybe"),
                                    List.of(Untyped.returnStatement(Untyped.reference("x")))
                                )
                            )
                        )
                    )
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addSealedInterfaceCase(interfaceType, recordType)
            .enterFunction(Types.OBJECT)
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("Record", Types.metaType(recordType), NullSource.INSTANCE)
            .addLocal("maybe", Types.BOOL, NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.context().typeOf("x", NullSource.INSTANCE), equalTo(interfaceType));
        assertThat(result.value(), contains(
            isA(TypedIfStatementNode.class)
        ));
    }

    @Test
    public void whenConditionIsNegatedInstanceOfCheckWithReturningBodyThenFollowingStatementsHaveNarrowedTypeForVariable() {
        var namespaceName = NamespaceName.fromParts("example");
        var interfaceType = Types.interfaceType(namespaceName, "Interface");
        var recordType = Types.recordType(namespaceName, "Record");
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(
                    Untyped.logicalNot(Untyped.instanceOf(
                        Untyped.reference("x"),
                        Untyped.typeLevelReference("Record")
                    )),
                    List.of(Untyped.returnStatement(Untyped.reference("x")))
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addSealedInterfaceCase(interfaceType, recordType)
            .enterFunction(Types.OBJECT)
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("Record", Types.metaType(recordType), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.context().typeOf("x", NullSource.INSTANCE), equalTo(recordType));
        assertThat(result.value(), contains(
            isA(TypedIfStatementNode.class),
            cast(TypedTypeNarrowNode.class, isTypedTypeNarrowNode("x", equalTo(recordType)))
        ));
    }
}
