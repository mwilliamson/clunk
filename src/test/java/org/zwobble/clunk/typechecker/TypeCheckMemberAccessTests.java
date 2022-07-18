package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedMemberAccessNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;

public class TypeCheckMemberAccessTests {
    @Test
    public void canTypeCheckMemberAccess() {
        var untypedNode = Untyped.memberAccess(Untyped.reference("id"), "value");
        var recordType = new RecordType(NamespaceName.fromParts("example"), "Id");
        var context = TypeCheckerContext.stub()
            .updateType("id", recordType, NullSource.INSTANCE)
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
        var recordType = new RecordType(NamespaceName.fromParts("example"), "Id");
        var context = TypeCheckerContext.stub()
            .updateType("id", recordType, NullSource.INSTANCE)
            .addFields(recordType, List.of(Typed.recordField("value", Typed.typeLevelInt())));

        var result = assertThrows(UnknownMemberError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(result.getType(), equalTo(recordType));
        assertThat(result.getMemberName(), equalTo("x"));
    }
}
