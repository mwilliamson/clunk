package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedSwitchCaseNode;
import org.zwobble.clunk.ast.typed.TypedSwitchNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;

public class TypeCheckSwitchTests {
    private final NamespaceId namespaceId = NamespaceId.source("example");
    private final InterfaceType interfaceType = Types.sealedInterfaceType(namespaceId, "X");
    private final RecordType recordType1 = Types.recordType(namespaceId, "A");
    private final RecordType recordType2 = Types.recordType(namespaceId, "B");

    @Test
    public void givenExpressionIsSealedInterfaceThenTypeChecksWhenCasesCoverAllSubtypes() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of()
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    List.of()
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedSwitchNode.class,
                has("expression", x -> x.expression(), isTypedReferenceNode().withName("x").withType(interfaceType)),
                has("cases", x -> x.cases(), isSequence(
                    instanceOf(
                        TypedSwitchCaseNode.class,
                        has("type", x -> x.type(), isTypedTypeLevelReferenceNode("A", recordType1))
                    ),
                    instanceOf(
                        TypedSwitchCaseNode.class,
                        has("type", x -> x.type(), isTypedTypeLevelReferenceNode("B", recordType2))
                    )
                ))
            )
        ));
    }

    @Test
    public void whenExpressionIsNotSealedInterfaceThenErrorIsThrown() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of()
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", Types.INT, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addSubtypeRelation(recordType1, interfaceType);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(SealedInterfaceTypeSet.INSTANCE));
        assertThat(result.getActual(), equalTo(Types.INT));
    }

    @Test
    public void whenSwitchIsNotExhaustiveThenErrorIsThrown() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of()
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2);

        var result = assertThrows(
            SwitchIsNotExhaustiveError.class,
            () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context)
        );

        assertThat(result.getUnhandledTypes(), isSequence(equalTo(recordType2)));
    }

    @Test
    public void whenSwitchHasImpossibleCaseThenErrorIsThrown() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of()
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    List.of()
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1);

        var result = assertThrows(
            InvalidCaseTypeError.class,
            () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context)
        );

        assertThat(result.getExpressionType(), equalTo(interfaceType));
        assertThat(result.getCaseType(), equalTo(recordType2));
    }

    @Test
    public void whenAllCasesReturnThenSwitchReturns() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of(Untyped.returnStatement(Untyped.boolFalse()))
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    List.of(Untyped.returnStatement(Untyped.boolTrue()))
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2)
            .enterFunction(Types.BOOL);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returnBehaviour(), equalTo(ReturnBehaviour.ALWAYS));
    }

    @Test
    public void typeOfSwitchExpressionIsNarrowedInCaseBody() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of(
                        Untyped.expressionStatement(Untyped.reference("x"))
                    )
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    List.of(
                        Untyped.expressionStatement(Untyped.reference("x"))
                    )
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedSwitchNode.class,
                has("cases", x -> x.cases(), isSequence(
                    instanceOf(
                        TypedSwitchCaseNode.class,
                        has("body", x -> x.body(), isSequence(
                            isTypedExpressionStatementNode(isTypedReferenceNode().withName("x").withType(recordType1))
                        ))
                    ),
                    instanceOf(
                        TypedSwitchCaseNode.class,
                        has("body", x -> x.body(), isSequence(
                            isTypedExpressionStatementNode(isTypedReferenceNode().withName("x").withType(recordType2))
                        ))
                    )
                ))
            )
        ));
    }

    @Test
    public void whenAllBranchesReturnThenSwitchReturns() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of(
                        Untyped.returnStatement(Untyped.intLiteral())
                    )
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    List.of(
                        Untyped.returnStatement(Untyped.intLiteral())
                    )
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2)
            .enterFunction(Types.INT);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedSwitchNode.class,
                has("returns", x -> x.returns(), equalTo(true))
            )
        ));
    }

    @Test
    public void whenAllBranchesDoNotReturnThenSwitchDoesNotReturn() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of(
                        Untyped.expressionStatement(Untyped.intLiteral())
                    )
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    List.of()
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2)
            .enterFunction(Types.INT);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), isSequence(
            instanceOf(
                TypedSwitchNode.class,
                has("returns", x -> x.returns(), equalTo(false))
            )
        ));
    }

    @Test
    public void whenSomeCasesReturnAndSomeDoNotThenAnErrorIsThrown() {
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    List.of()
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    List.of(Untyped.returnStatement(Untyped.boolTrue()))
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2)
            .enterFunction(Types.BOOL);

        assertThrows(InconsistentSwitchCaseReturnError.class, () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context));
    }
}
