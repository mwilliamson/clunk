package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedFieldAccessNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedReferenceNode;

public class TypeCheckFieldAccessTests {
    @Test
    public void canTypeCheckCallToStaticFunction() {
        var untypedNode = Untyped.fieldAccess(Untyped.reference("id"), "value");
        var recordType = new RecordType(NamespaceName.fromParts("example"), "Id");
        var context = TypeCheckerContext.stub()
            .updateType("id", recordType, NullSource.INSTANCE)
            .addFields(recordType, List.of(Typed.recordField("value", Types.INT)));

        var result = TypeChecker.typeCheckExpression(untypedNode, context);

        assertThat(result, isTypedFieldAccessNode()
            .withReceiver(isTypedReferenceNode().withName("id").withType(recordType))
            .withFieldName(equalTo("value"))
            .withType(Types.INT)
        );
    }
}
