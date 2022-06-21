package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckRecordTests {
    @Test
    public void recordIsTypeChecked() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.staticReference("String")))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub()
        );

        assertThat(result.typedNode(), allOf(
            has("name", equalTo("Example")),
            has("fields", contains(
                allOf(
                    has("name", equalTo("x")),
                    has("type", has("type", equalTo(StringType.INSTANCE)))
                )
            ))
        ));
    }
}
