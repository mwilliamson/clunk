package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedInterfaceNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.TypeLevelValueType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.matchers.MapEntryMatcher.isMapEntry;
import static org.zwobble.clunk.matchers.OptionalMatcher.present;
import static org.zwobble.clunk.matchers.TypeMatchers.isMetaType;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckInterfaceTests {
    @Test
    public void interfaceTypeIsAddedToEnvironment() {
        var untypedNode = Untyped.interface_("DocumentElement");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.context().typeOf("DocumentElement", NullSource.INSTANCE),
            cast(
                TypeLevelValueType.class,
                has("value", cast(
                    InterfaceType.class,
                    has("namespaceName", equalTo(NamespaceName.fromParts("a", "b"))),
                    has("name", equalTo("DocumentElement"))
                ))
            )
        );
    }

    @Test
    public void canTypeCheckedEmptyInterface() {
        var untypedNode = Untyped.interface_("DocumentElement");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.typedNode(), cast(
            TypedInterfaceNode.class,
            has("name", equalTo("DocumentElement")),
            has("type", cast(
                InterfaceType.class,
                has("namespaceName", equalTo(NamespaceName.fromParts("a", "b"))),
                has("name", equalTo("DocumentElement"))
            ))
        ));
    }

    @Test
    public void addsNamespaceField() {
        var untypedNode = Untyped.interface_("DocumentElement");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.fieldType(),
            present(isMapEntry(
                equalTo("DocumentElement"),
                isMetaType(cast(
                    InterfaceType.class,
                    has("name", equalTo("DocumentElement"))
                ))
            ))
        );
    }
}
