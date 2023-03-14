package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedInstanceOfNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelReferenceNode;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckInstanceOfTests {
    private final NamespaceId namespaceId = NamespaceId.source("example");
    private final InterfaceType interfaceType = Types.sealedInterfaceType(namespaceId, "X");
    private final RecordType recordType1 = Types.recordType(namespaceId, "A");
    private final RecordType recordType2 = Types.recordType(namespaceId, "B");

    @Test
    public void givenExpressionIsSealedInterfaceThenCanTestExpressionIsInstanceOfSubtype() {
        var untypedNode = Untyped.instanceOf(
            Untyped.reference("x"),
            Untyped.typeLevelReference("A")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addSealedInterfaceCase(interfaceType, recordType1)
            .addSealedInterfaceCase(interfaceType, recordType2);

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, instanceOf(
            TypedInstanceOfNode.class,
            has("expression", x -> x.expression(), isTypedReferenceNode().withName("x").withType(interfaceType)),
            has("typeExpression", x -> x.typeExpression(), isTypedTypeLevelReferenceNode("A", recordType1))
        ));
    }

    @Test
    public void whenExpressionIsNotSealedInterfaceThenErrorIsThrown() {
        var untypedNode = Untyped.instanceOf(
            Untyped.reference("x"),
            Untyped.typeLevelReference("A")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", Types.INT, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE)
            .addSubtypeRelation(recordType1, interfaceType);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(SealedInterfaceTypeSet.INSTANCE));
        assertThat(result.getActual(), equalTo(Types.INT));
    }

    @Test
    public void whenTypeExpressionIsNotSubtypeOfExpressionTypeThenErrorIsThrown() {
        var untypedNode = Untyped.instanceOf(
            Untyped.reference("x"),
            Untyped.typeLevelReference("A")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("A", Types.metaType(recordType1), NullSource.INSTANCE);

        var result = assertThrows(
            InvalidCaseTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpressionType(), equalTo(interfaceType));
        assertThat(result.getCaseType(), equalTo(recordType1));
    }

    @Test
    public void whenTypeExpressionIsNotTypeThenErrorIsThrown() {
        var untypedNode = Untyped.instanceOf(
            Untyped.reference("x"),
            Untyped.typeLevelReference("L")
        );
        var context = TypeCheckerContext.stub()
            .addLocal("x", interfaceType, NullSource.INSTANCE)
            .addLocal("L", Types.typeConstructorType(Types.LIST_CONSTRUCTOR), NullSource.INSTANCE)
            .addSubtypeRelation(recordType1, interfaceType);

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> TypeChecker.typeCheckExpression(untypedNode, context)
        );

        assertThat(result.getExpected(), equalTo(MetaTypeTypeSet.INSTANCE));
        assertThat(result.getActual(), equalTo(Types.typeConstructorType(Types.LIST_CONSTRUCTOR)));
    }
}
