package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.MetaType;
import org.zwobble.clunk.types.NamespaceName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckInterfaceTests {
    @Test
    public void interfaceTypeIsAddedToEnvironment() {
        var untypedNode = Untyped.interface_("DocumentElement");

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"))
        );

        assertThat(
            result.context().typeOf("DocumentElement", NullSource.INSTANCE),
            cast(
                MetaType.class,
                has("type", cast(
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

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"))
        );

        assertThat(result.typedNode(), has("name", equalTo("DocumentElement")));
    }
}
