package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedMemberAccessNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;

public class TypeCheckMemberAccessTests {
    @Test
    public void canTypeCheckMemberAccess() {
        var untypedNode = Untyped.memberAccess(Untyped.reference("id"), "value");
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var context = TypeCheckerContext.stub()
            .addLocal("id", recordType, NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.ofEntries(Map.entry("value", Types.INT)));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedMemberAccessNode()
            .withReceiver(isTypedReferenceNode().withName("id").withType(recordType))
            .withMemberName(equalTo("value"))
            .withType(Types.INT)
        );
    }

    @Test
    public void whenMemberNameIsUnknownThenErrorIsThrown() {
        var untypedNode = Untyped.memberAccess(Untyped.reference("id"), "x");
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var context = TypeCheckerContext.stub()
            .addLocal("id", recordType, NullSource.INSTANCE);

        var result = assertThrows(UnknownMemberError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(result.getType(), equalTo(recordType));
        assertThat(result.getMemberName(), equalTo("x"));
    }

    @Test
    public void whenReceiverIsConstructedTypeThenCanTypeCheckMemberAccess() {
        var untypedNode = Untyped.memberAccess(Untyped.reference("values"), "first");
        var genericType = Types.recordType(NamespaceId.source("example"), "List");
        var typeParameter = TypeParam.covariant(NamespaceId.source("example"), "List", "T");
        var typeConstructor = new TypeConstructor(
            List.of(typeParameter),
            genericType
        );
        var constructedType = Types.construct(typeConstructor, List.of(Types.INT));
        var context = TypeCheckerContext.stub()
            .addLocal("values", constructedType, NullSource.INSTANCE)
            .addMemberTypes(genericType, Map.ofEntries(Map.entry("first", typeParameter)));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedMemberAccessNode()
            .withReceiver(isTypedReferenceNode().withName("values").withType(constructedType))
            .withMemberName(equalTo("first"))
            .withType(Types.INT)
        );
    }
}
