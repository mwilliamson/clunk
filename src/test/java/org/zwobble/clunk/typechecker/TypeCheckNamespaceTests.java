package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.UntypedNamespaceNode;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.types.NamespaceName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedRecordNode;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckNamespaceTests {
    @Test
    public void namespaceIsTypeChecked() {
        var untypedNode = UntypedNamespaceNode
            .builder(NamespaceName.parts("example", "project"))
            .addStatement(UntypedRecordNode.builder("X").build())
            .build();

        var result = TypeChecker.typeCheckNamespace(untypedNode, TypeCheckerContext.stub());

        assertThat(result, allOf(
            has("name", equalTo(NamespaceName.parts("example", "project"))),
            has("statements", contains(
                isTypedRecordNode(has("name", equalTo("X")))
            ))
        ));
    }
}
