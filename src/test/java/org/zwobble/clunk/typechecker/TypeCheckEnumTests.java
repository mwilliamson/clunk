package org.zwobble.clunk.typechecker;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedEnumNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.EnumType;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.TypeLevelValueType;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckEnumTests {
    @Test
    public void enumTypeIsAddedToEnvironment() {
        var untypedNode = Untyped.enum_("NoteType");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.context().typeOf("NoteType", NullSource.INSTANCE),
            cast(
                TypeLevelValueType.class,
                has("value", isEnumType(NamespaceName.fromParts("a", "b"), "NoteType"))
            )
        );
    }

    @Test
    public void addsNamespaceField() {
        var untypedNode = Untyped.enum_("NoteType");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.fieldType(),
            equalTo(Optional.of(Map.entry(
                "NoteType",
                Types.metaType(Types.enumType(NamespaceName.fromParts("a", "b"), "NoteType", List.of()))
            )))
        );
    }

    @Test
    public void enumIsTypeChecked() {
        var untypedNode = Untyped.enum_("NoteType");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedEnumNode) result.typedNode();
        assertThat(typedNode, allOf(
            has("type", isEnumType(NamespaceName.fromParts("a", "b"), "NoteType"))
        ));
    }

    private Matcher<?> isEnumType(NamespaceName namespaceName, String name) {
        return cast(EnumType.class, has("namespaceName", equalTo(namespaceName)), has("name", equalTo(name)));
    }
}
