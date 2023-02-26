package org.zwobble.clunk.typechecker;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedEnumNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.matchers.MapEntryMatcher.isMapEntry;
import static org.zwobble.clunk.matchers.OptionalMatcher.present;
import static org.zwobble.clunk.matchers.TypeMatchers.isMetaType;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckEnumTests {
    @Test
    public void enumTypeIsAddedToEnvironment() {
        var untypedNode = Untyped.enum_("NoteType");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.context().typeOf("NoteType", NullSource.INSTANCE),
            cast(
                TypeLevelValueType.class,
                has("value", isEnumType(NamespaceId.source("a", "b"), "NoteType"))
            )
        );
    }

    @Test
    public void addsNamespaceField() {
        var untypedNode = Untyped.enum_("NoteType");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.fieldType(),
            present(isMapEntry(
                equalTo("NoteType"),
                isMetaType(isEnumType(NamespaceId.source("a", "b"), "NoteType"))
            ))
        );
    }

    @Test
    public void enumIsTypeChecked() {
        var untypedNode = Untyped.enum_("NoteType");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedEnumNode) result.typedNode();
        assertThat(typedNode, allOf(
            has("type", isEnumType(NamespaceId.source("a", "b"), "NoteType"))
        ));
    }

    private Matcher<Type> isEnumType(NamespaceId namespaceId, String name) {
        return cast(EnumType.class, has("namespaceId", equalTo(namespaceId)), has("name", equalTo(name)));
    }
}
