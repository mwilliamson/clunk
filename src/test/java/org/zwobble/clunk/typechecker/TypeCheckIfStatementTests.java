package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedIfStatementNode;
import org.zwobble.clunk.ast.typed.TypedTypeNarrowNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.precisely.Matchers.instanceOf;
import static org.zwobble.precisely.Matchers.has;

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

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedIfStatementNode.class,
                has("conditionalBranches", x -> x.conditionalBranches(), isSequence(
                    has("condition", x -> x.condition(), isTypedBoolLiteralNode(false))
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

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedIfStatementNode.class,
                has("conditionalBranches", x -> x.conditionalBranches(), isSequence(
                    has("body", x -> x.body(), isSequence(isTypedExpressionStatementNode(isTypedIntLiteralNode(42))))
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

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedIfStatementNode.class,
                has("elseBody", x -> x.elseBody(), isSequence(isTypedExpressionStatementNode(isTypedIntLiteralNode(42))))
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
        var namespaceId = NamespaceId.source("example");
        var interfaceType = Types.sealedInterfaceType(namespaceId, "Interface");
        var recordType = Types.recordType(namespaceId, "Record");
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

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedIfStatementNode.class,
                has("conditionalBranches", x -> x.conditionalBranches(), isSequence(
                    has("body", x -> x.body(), isSequence(
                        isTypedTypeNarrowNode("x", equalTo(recordType)),
                        isTypedReturnNode().withExpression(isTypedReferenceNode().withType(recordType))
                    ))
                )),
                has("elseBody", x -> x.elseBody(), isSequence(
                    isTypedReturnNode().withExpression(isTypedReferenceNode().withType(interfaceType)))
                )
            )
        ));
    }

    @Test
    public void whenConditionIsNegatedInstanceOfCheckThenFollowingBranchesHaveNarrowedTypeForVariable() {
        var namespaceId = NamespaceId.source("example");
        var interfaceType = Types.sealedInterfaceType(namespaceId, "Interface");
        var recordType = Types.recordType(namespaceId, "Record");
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

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedIfStatementNode.class,
                has("conditionalBranches", x -> x.conditionalBranches(), isSequence(
                    has("body", x -> x.body(), isSequence(isTypedExpressionStatementNode(isTypedReferenceNode().withType(interfaceType)))),
                    has("body", x -> x.body(), isSequence(isTypedExpressionStatementNode(isTypedReferenceNode().withType(interfaceType)))),
                    has("body", x -> x.body(), isSequence(
                        isTypedTypeNarrowNode("x", equalTo(recordType)),
                        isTypedExpressionStatementNode(isTypedReferenceNode().withType(recordType))
                    ))
                )),
                has("elseBody", x -> x.elseBody(), isSequence(
                    isTypedTypeNarrowNode("x", equalTo(recordType)),
                    isTypedExpressionStatementNode(isTypedReferenceNode().withType(recordType))
                ))
            )
        ));
    }

    @Test
    public void whenBodyIsEmptyThenNoTypeNarrowIsInserted() {
        var namespaceId = NamespaceId.source("example");
        var interfaceType = Types.sealedInterfaceType(namespaceId, "Interface");
        var recordType = Types.recordType(namespaceId, "Record");
        var untypedNode = Untyped.ifStatement(
            List.of(
                Untyped.conditionalBranch(
                    Untyped.logicalNot(Untyped.instanceOf(
                        Untyped.reference("x"),
                        Untyped.typeLevelReference("Record")
                    )),
                    List.of(Untyped.expressionStatement(Untyped.reference("x")))
                ),
                Untyped.conditionalBranch(
                    Untyped.boolFalse(),
                    List.of()
                )
            ),
            List.of()
        );
        var context = TypeCheckerContext.stub()
            .addSealedInterfaceCase(interfaceType, recordType)
            .enterFunction(Types.OBJECT)
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("Record", Types.metaType(recordType), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedIfStatementNode.class,
                has("conditionalBranches", x -> x.conditionalBranches(), isSequence(
                    has("body", x -> x.body(), isSequence(isTypedExpressionStatementNode(isTypedReferenceNode().withType(interfaceType)))),
                    has("body", x -> x.body(), isSequence())
                )),
                has("elseBody", x -> x.elseBody(), isSequence())
            )
        ));
    }

    @Test
    public void whenConditionIsNegatedInstanceOfCheckWithNonReturningBodyThenFollowingStatementsHaveUnchangedTypeForVariable() {
        var namespaceId = NamespaceId.source("example");
        var interfaceType = Types.sealedInterfaceType(namespaceId, "Interface");
        var recordType = Types.recordType(namespaceId, "Record");
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
        assertThat(result.value(), isSequence(
            instanceOf(TypedIfStatementNode.class)
        ));
    }

    @Test
    public void whenConditionIsNegatedInstanceOfCheckWithReturningBodyThenFollowingStatementsHaveNarrowedTypeForVariable() {
        var namespaceId = NamespaceId.source("example");
        var interfaceType = Types.sealedInterfaceType(namespaceId, "Interface");
        var recordType = Types.recordType(namespaceId, "Record");
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
        assertThat(result.value(), isSequence(
            instanceOf(TypedIfStatementNode.class),
            instanceOf(TypedTypeNarrowNode.class, isTypedTypeNarrowNode("x", equalTo(recordType)))
        ));
    }
}
