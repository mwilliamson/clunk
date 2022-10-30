package org.zwobble.clunk.typechecker;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;

public class TypeCheckRecordTests {
    @Test
    public void recordTypeIsAddedToEnvironment() {
        var untypedNode = UntypedRecordNode.builder("Example").build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

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
    public void addsNamespaceField() {
        var untypedNode = UntypedRecordNode.builder("Example").build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.fieldType(),
            equalTo(Optional.of(Map.entry(
                "Example",
                Types.metaType(Types.recordType(NamespaceName.fromParts("a", "b"), "Example"))
            )))
        );
    }

    @Test
    public void recordIsTypeChecked() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode, allOf(
            has("name", equalTo("Example")),
            has("type", isRecordType(NamespaceName.fromParts("a", "b"), "Example"))
        ));
    }

    @Test
    public void fieldsAreIncludedInTypedNode() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("String")))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.fields(), contains(
            allOf(
                has("name", equalTo("x")),
                has("type", has("value", equalTo(Types.STRING)))
            )
        ));
    }

    @Test
    public void fieldsForTypeAreUpdated() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("String")))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(result.context().fieldsOf(typedNode.type()), contains(
            allOf(
                has("name", equalTo("x")),
                has("type", isTypedTypeLevelExpressionNode(StringType.INSTANCE))
            )
        ));
    }

    @Test
    public void fieldsAreAddedToMemberTypes() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("String")))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(result.context().memberType(typedNode.type(), "x"), equalTo(Optional.of(Types.STRING)));
    }

    @Test
    public void propertiesAreIncludedInTypedNode() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addBodyDeclaration(Untyped.property(
                "x",
                Untyped.typeLevelReference("String"),
                List.of(Untyped.returnStatement(Untyped.string("hello")))
            ))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.body(), contains(
            allOf(
                has("name", equalTo("x")),
                has("type", has("value", equalTo(Types.STRING))),
                has("body", contains(
                    isTypedReturnNode().withExpression(isTypedStringLiteralNode("hello"))
                ))
            )
        ));
    }

    @Test
    public void propertiesAreAddedToMemberTypes() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addBodyDeclaration(Untyped.property(
                "x",
                Untyped.typeLevelReference("String"),
                List.of(Untyped.returnStatement(Untyped.string()))
            ))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(result.context().memberType(typedNode.type(), "x"), equalTo(Optional.of(Types.STRING)));
    }

    @Test
    public void canAccessFieldsInsideProperties() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("String")))
            .addBodyDeclaration(Untyped.property(
                "y",
                Untyped.typeLevelReference("String"),
                List.of(Untyped.returnStatement(Untyped.reference("x")))
            ))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.body(), contains(
            allOf(
                has("body", contains(
                    isTypedReturnNode().withExpression(isTypedMemberReferenceNode().withName("x").withType(Types.STRING))
                ))
            )
        ));
    }

    @Test
    public void canAccessPropertiesInsideProperties() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addBodyDeclaration(Untyped.property(
                "x",
                Untyped.typeLevelReference("String"),
                List.of(Untyped.returnStatement(Untyped.string("hello")))
            ))
            .addBodyDeclaration(Untyped.property(
                "y",
                Untyped.typeLevelReference("String"),
                List.of(Untyped.returnStatement(Untyped.reference("x")))
            ))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.body(), contains(
            has("name", equalTo("x")),
            allOf(
                has("name", equalTo("y")),
                has("body", contains(
                    isTypedReturnNode().withExpression(isTypedMemberReferenceNode().withName("x").withType(Types.STRING))
                ))
            )
        ));
    }

    @Test
    public void whenRecordHasTwoMembersWithTheSameNameThenAnErrorIsThrown() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("Int")))
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("Int")))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceName.fromParts("a", "b"));

        var result = assertThrows(
            FieldIsAlreadyDefinedError.class,
            () -> typeCheckNamespaceStatementAllPhases(untypedNode, context)
        );

        assertThat(result.getFieldName(), equalTo("x"));
    }

    @Test
    public void subtypeRelationsAreUpdated() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.typeLevelReference("Person"))
            .build();
        var interfaceType = Types.sealedInterfaceType(NamespaceName.fromParts("a", "b"), "Person");
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceName.fromParts("a", "b"))
            .addLocal("Person", Types.metaType(interfaceType), NullSource.INSTANCE);

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.typedNode(), has("supertypes", contains(
            has("value", isInterfaceType(NamespaceName.fromParts("a", "b"), "Person"))
        )));
        assertThat(result.context().sealedInterfaceCases(interfaceType), containsInAnyOrder(
            isRecordType(NamespaceName.fromParts("a", "b"), "User")
        ));
    }

    @Test
    public void whenSupertypeIsNotInterfaceThenErrorIsThrown() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.typeLevelReference("Bool"))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceName.fromParts("a", "b"))
            .addLocal("Bool", Types.metaType(Types.BOOL), NullSource.INSTANCE);

        assertThrows(
            CannotExtendFinalTypeError.class,
            () -> typeCheckNamespaceStatementAllPhases(untypedNode, context)
        );
    }

    @Test
    public void whenSupertypeIsSealedInterfaceFromDifferentNamespaceThenErrorIsThrown() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.typeLevelReference("Person"))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceName.fromParts("a", "b"))
            .addLocal("Person", Types.metaType(Types.sealedInterfaceType(NamespaceName.fromParts("d", "e"), "Person")), NullSource.INSTANCE);

        assertThrows(
            CannotExtendSealedInterfaceFromDifferentNamespaceError.class,
            () -> typeCheckNamespaceStatementAllPhases(untypedNode, context)
        );
    }

    private Matcher<?> isInterfaceType(NamespaceName namespaceName, String name) {
        return cast(InterfaceType.class, has("namespaceName", equalTo(namespaceName)), has("name", equalTo(name)));
    }

    private Matcher<Type> isRecordType(NamespaceName namespaceName, String name) {
        return cast(RecordType.class, has("namespaceName", equalTo(namespaceName)), has("name", equalTo(name)));
    }
}
