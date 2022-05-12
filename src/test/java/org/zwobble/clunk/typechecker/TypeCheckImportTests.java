package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Types;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedImportNode;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckImportTests {
    // TODO: test unknown namespace
    // TODO: test import of namespace (no field)
    @Test
    public void importedFieldIsTypeChecked() {
        var untypedNode = Untyped.import_(NamespaceName.fromParts("x", "y"), "IntAlias");
        var namespaceType = new NamespaceType(
            NamespaceName.fromParts("x", "y"),
            Map.of("IntAlias", Types.metaType(Types.INT))
        );
        var context = TypeCheckerContext.stub()
            .updateNamespaceType(namespaceType);

        var result = TypeChecker.typeCheckImport(untypedNode, context);

        assertThat(result.node(), isTypedImportNode(allOf(
            has("namespaceName", equalTo(NamespaceName.fromParts("x", "y"))),
            has("fieldName", equalTo(Optional.of("IntAlias"))),
            has("type", equalTo(Types.metaType(Types.INT)))
        )));
    }

    @Test
    public void importedFieldIsAddedToEnvironment() {
        var untypedNode = Untyped.import_(NamespaceName.fromParts("x", "y"), "IntAlias");
        var namespaceType = new NamespaceType(
            NamespaceName.fromParts("x", "y"),
            Map.of("IntAlias", Types.metaType(Types.INT))
        );
        var context = TypeCheckerContext.stub()
            .updateNamespaceType(namespaceType);

        var result = TypeChecker.typeCheckImport(untypedNode, context);

        assertThat(result.context().typeOf("IntAlias", NullSource.INSTANCE), equalTo(Types.metaType(Types.INT)));
    }

    @Test
    public void whenFieldIsUnknownThenErrorIsThrown() {
        var untypedNode = Untyped.import_(NamespaceName.fromParts("x", "y"), "IntAlias");
        var namespaceType = new NamespaceType(
            NamespaceName.fromParts("x", "y"),
            Map.of("Int", Types.metaType(Types.INT))
        );
        var context = TypeCheckerContext.stub()
            .updateNamespaceType(namespaceType);

        var error = assertThrows(SourceError.class, () -> TypeChecker.typeCheckImport(untypedNode, context));

        assertThat(error.getMessage(), equalTo("unknown field IntAlias on namespace x/y"));
    }
}
