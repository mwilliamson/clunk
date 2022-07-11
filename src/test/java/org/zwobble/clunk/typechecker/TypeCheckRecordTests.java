package org.zwobble.clunk.typechecker;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelExpressionNode;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckRecordTests {
    @Test
    public void recordTypeIsAddedToEnvironment() {
        var untypedNode = UntypedRecordNode.builder("Example").build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"))
        );

        assertThat(
            result.context().typeOf("Example", NullSource.INSTANCE),
            cast(
                TypeLevelValueType.class,
                has("value", cast(
                    RecordType.class,
                    has("namespaceName", equalTo(NamespaceName.fromParts("a", "b"))),
                    has("name", equalTo("Example"))
                ))
            )
        );
    }

    @Test
    public void recordIsTypeChecked() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.staticReference("String")))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"))
        );

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode, allOf(
            has("name", equalTo("Example")),
            has("type", isRecordType(NamespaceName.fromParts("a", "b"), "Example"))
        ));
        assertThat(result.context().fieldsLookup().fieldsOf(typedNode.type()), contains(
            allOf(
                has("name", equalTo("x")),
                has("type", isTypedTypeLevelExpressionNode(StringType.INSTANCE))
            )
        ));
    }

    @Test
    public void subtypeRelationsAreUpdated() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.staticReference("Person"))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub()
                .enterNamespace(NamespaceName.fromParts("a", "b"))
                .updateType("Person", Types.metaType(Types.interfaceType(NamespaceName.fromParts("a", "b"), "Person")), NullSource.INSTANCE)
        );

        assertThat(result.context().subtypeRelations(), containsInAnyOrder(
            allOf(
                has("subtype", isRecordType(NamespaceName.fromParts("a", "b"), "User")),
                has("supertype", isInterfaceType(NamespaceName.fromParts("a", "b"), "Person"))
            )
        ));
    }

    @Test
    public void whenSupertypeIsNotInterfaceThenErrorIsThrown() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.staticReference("Bool"))
            .build();

        assertThrows(
            CannotExtendFinalTypeError.class,
            () -> TypeChecker.typeCheckNamespaceStatement(
                untypedNode,
                TypeCheckerContext.stub()
                    .enterNamespace(NamespaceName.fromParts("a", "b"))
                    .updateType("Bool", Types.metaType(Types.BOOL), NullSource.INSTANCE)
            )
        );
    }

    @Test
    public void whenSupertypeIsSealedInterfaceFromDifferentNamespaceThenErrorIsThrown() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.staticReference("Person"))
            .build();

        assertThrows(
            CannotExtendSealedInterfaceFromDifferentNamespaceError.class,
            () -> TypeChecker.typeCheckNamespaceStatement(
                untypedNode,
                TypeCheckerContext.stub()
                    .enterNamespace(NamespaceName.fromParts("a", "b"))
                    .updateType("Person", Types.metaType(Types.interfaceType(NamespaceName.fromParts("d", "e"), "Person")), NullSource.INSTANCE)
            )
        );
    }

    private Matcher<?> isInterfaceType(NamespaceName namespaceName, String name) {
        return cast(InterfaceType.class, has("namespaceName", equalTo(namespaceName)), has("name", equalTo(name)));
    }

    private Matcher<?> isRecordType(NamespaceName namespaceName, String name) {
        return cast(RecordType.class, has("namespaceName", equalTo(namespaceName)), has("name", equalTo(name)));
    }
}
