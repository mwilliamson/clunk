package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedNamespaceNode;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Types;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckNamespaceTests {
    @Test
    public void namespaceIsTypeChecked() {
        var untypedNode = UntypedNamespaceNode
            .builder(NamespaceName.fromParts("example", "project"))
            .addStatement(UntypedRecordNode.builder("X").build())
            .build();

        var result = TypeChecker.typeCheckNamespace(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), allOf(
            has("name", equalTo(NamespaceName.fromParts("example", "project"))),
            has("statements", contains(
                isTypedRecordNode(has("name", equalTo("X")))
            ))
        ));
    }

    @Test
    public void importedFieldIsAddedToEnvironment() {
        var untypedNode = UntypedNamespaceNode.builder(NamespaceName.fromParts("example", "project"))
            .addImport(Untyped.import_(NamespaceName.fromParts("x", "y"), "IntAlias"))
            .addStatement(
                UntypedRecordNode.builder("X")
                    .addField(Untyped.recordField("f", Untyped.staticReference("IntAlias"))).build()
            )
            .build();
        var namespaceType = new NamespaceType(
            NamespaceName.fromParts("x", "y"),
            Map.of("IntAlias", Types.metaType(Types.INT))
        );
        var context = TypeCheckerContext.stub()
            .updateNamespaceType(namespaceType);

        var result = TypeChecker.typeCheckNamespace(untypedNode, context);

        assertThat(result.typedNode(), allOf(
            has("imports", contains(
                isTypedImportNode(allOf(
                    has("namespaceName", equalTo(NamespaceName.fromParts("x", "y"))),
                    has("fieldName", equalTo(Optional.of("IntAlias")))
                ))
            ))
        ));
        var typedRecordNode = (TypedRecordNode) result.typedNode().statements().get(0);
        assertThat(result.context().fieldsLookup().fieldsOf(typedRecordNode.type()), contains(
            allOf(
                has("type", isTypedTypeLevelExpressionNode(Types.INT))
            )
        ));
    }
}
