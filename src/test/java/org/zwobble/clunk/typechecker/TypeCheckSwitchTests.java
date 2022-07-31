package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedSwitchCaseNode;
import org.zwobble.clunk.ast.typed.TypedSwitchNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.SealedInterfaceTypeSet;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelReferenceNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class TypeCheckSwitchTests {
    @Test
    public void givenExpressionIsSealedInterfaceThenTypeChecksWhenCasesCoverAllSubtypes() {
        var namespaceName = NamespaceName.fromParts("example");
        var interfaceType = Types.interfaceType(namespaceName, "X");
        var recordType1 = Types.recordType(namespaceName, "A");
        var recordType2 = Types.recordType(namespaceName, "B");
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    "a",
                    List.of()
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    "b",
                    List.of()
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.value(), cast(
            TypedSwitchNode.class,
            has("expression", isTypedReferenceNode().withName("x").withType(interfaceType)),
            has("cases", contains(
                cast(
                    TypedSwitchCaseNode.class,
                    has("type", isTypedTypeLevelReferenceNode("A", recordType1)),
                    has("variableName", equalTo("a"))
                ),
                cast(
                    TypedSwitchCaseNode.class,
                    has("type", isTypedTypeLevelReferenceNode("B", recordType2)),
                    has("variableName", equalTo("b"))
                )
            ))
        ));
    }

    @Test
    public void whenExpressionIsNotSealedInterfaceThenErrorIsThrown() {
        var namespaceName = NamespaceName.fromParts("example");
        var recordType1 = Types.recordType(namespaceName, "A");
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    "a",
                    List.of()
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", Types.INT, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckFunctionStatement(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(SealedInterfaceTypeSet.INSTANCE));
        assertThat(result.getActual(), equalTo(Types.INT));
    }

    @Test
    public void whenAtLeastOneCaseDoesNotReturnThenSwitchDoesNotReturn() {
        var namespaceName = NamespaceName.fromParts("example");
        var interfaceType = Types.interfaceType(namespaceName, "X");
        var recordType1 = Types.recordType(namespaceName, "A");
        var recordType2 = Types.recordType(namespaceName, "B");
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    "a",
                    List.of()
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    "b",
                    List.of(Untyped.returnStatement(Untyped.boolTrue()))
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .enterFunction(Types.BOOL);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returns(), equalTo(false));
    }

    @Test
    public void whenAllCasesReturnThenSwitchReturns() {
        var namespaceName = NamespaceName.fromParts("example");
        var interfaceType = Types.interfaceType(namespaceName, "X");
        var recordType1 = Types.recordType(namespaceName, "A");
        var recordType2 = Types.recordType(namespaceName, "B");
        var untypedNode = Untyped.switchStatement(
            Untyped.reference("x"),
            List.of(
                Untyped.switchCase(
                    Untyped.typeLevelReference("A"),
                    "a",
                    List.of(Untyped.returnStatement(Untyped.boolFalse()))
                ),
                Untyped.switchCase(
                    Untyped.typeLevelReference("B"),
                    "b",
                    List.of(Untyped.returnStatement(Untyped.boolTrue()))
                )
            )
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addLocal("B", Types.metaType(recordType2), NullSource.INSTANCE)
            .enterFunction(Types.BOOL);

        var result = TypeChecker.typeCheckFunctionStatement(untypedNode, context);

        assertThat(result.returns(), equalTo(true));
    }
}
