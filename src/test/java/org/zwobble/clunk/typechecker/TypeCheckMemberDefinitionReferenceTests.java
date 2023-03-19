package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedMemberDefinitionReferenceNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeCheckMemberDefinitionReferenceTests {
    @Test
    public void canTypeCheckMemberDefinitionReference() {
        var untypedNode = Untyped.memberDefinitionReference(Untyped.reference("Id"), "value");
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var context = TypeCheckerContext.stub()
            .addLocal("Id", Types.metaType(recordType), NullSource.INSTANCE)
            .addMemberTypes(recordType, Map.ofEntries(Map.entry("value", Types.INT)));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedMemberDefinitionReferenceNode()
            .withReceiver(isTypedReferenceNode().withName("Id"))
            .withMemberName(equalTo("value"))
            .withType(Types.member(recordType, Types.INT))
        );
    }

    @Test
    public void whenMemberNameIsUnknownThenErrorIsThrown() {
        var untypedNode = Untyped.memberDefinitionReference(Untyped.reference("Id"), "x");
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var context = TypeCheckerContext.stub()
            .addLocal("Id", Types.metaType(recordType), NullSource.INSTANCE);

        var result = assertThrows(UnknownMemberError.class, () -> TypeChecker.typeCheckExpression(untypedNode, context));

        assertThat(result.getType(), equalTo(recordType));
        assertThat(result.getMemberName(), equalTo("x"));
    }
}
