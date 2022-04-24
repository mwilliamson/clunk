package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Types;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedImportNode;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckImportTests {
    // TODO: test unknown namespace
    // TODO: test unknown field
    // TODO: test import of namespace (no field)
    @Test
    public void importedFieldIsAddedToEnvironment() {
        var untypedNode = Untyped.import_(NamespaceName.parts("x", "y"), "IntAlias");
        var namespaceType = new NamespaceType(
            NamespaceName.parts("x", "y"),
            Map.of("IntAlias", Types.metaType(Types.INT))
        );
        var context = TypeCheckerContext.stub()
            .updateNamespaceType(namespaceType);

        var result = TypeChecker.typeCheckImport(untypedNode, context);

        assertThat(result.node(), isTypedImportNode(allOf(
            has("namespaceName", equalTo(NamespaceName.parts("x", "y"))),
            has("fieldName", equalTo(Optional.of("IntAlias")))
        )));
        assertThat(result.context().typeOf("IntAlias", NullSource.INSTANCE), equalTo(Types.metaType(Types.INT)));
    }
}
