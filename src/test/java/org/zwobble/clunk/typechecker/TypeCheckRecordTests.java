package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedPropertyNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;
import org.zwobble.precisely.Matcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.*;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckRecordTests {
    @Test
    public void recordTypeIsAddedToEnvironment() {
        var untypedNode = UntypedRecordNode.builder("Example").build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.context().typeOf("Example", NullSource.INSTANCE),
            instanceOf(
                TypeLevelValueType.class,
                has("value", x -> x.value(), instanceOf(
                    RecordType.class,
                    has("namespaceId", x -> x.namespaceId(), equalTo(NamespaceId.source("a", "b"))),
                    has("name", x -> x.name(), equalTo("Example"))
                ))
            )
        );
    }

    @Test
    public void addsNamespaceField() {
        var untypedNode = UntypedRecordNode.builder("Example").build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.fieldType(),
            equalTo(Optional.of(Map.entry(
                "Example",
                Types.metaType(Types.recordType(NamespaceId.source("a", "b"), "Example"))
            )))
        );
    }

    @Test
    public void recordIsTypeChecked() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode, allOf(
            has("name", x -> x.name(), equalTo("Example")),
            has("type", x -> x.type(), isRecordType(NamespaceId.source("a", "b"), "Example"))
        ));
    }

    @Test
    public void fieldsAreIncludedInTypedNode() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("String")))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.fields(), isSequence(
            allOf(
                has("name", x -> x.name(), equalTo("x")),
                has("type", x -> x.type(), has("value", x -> x.value(), equalTo(Types.STRING)))
            )
        ));
    }

    @Test
    public void constructorForTypeIsSet() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("String")))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(result.context().constructorType(typedNode.type()), isOptionalOf(instanceOf(
            ConstructorType.class,
            has("typeLevelParams", x -> x.typeLevelParams(), equalTo(Optional.empty())),
            has("positionalParams", x -> x.positionalParams(), isSequence(equalTo(Types.STRING))),
            has("returnType", x -> x.returnType(), equalTo(typedNode.type()))
        )));
    }

    @Test
    public void fieldsAreAddedToMemberTypes() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("String")))
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

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
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.body(), isSequence(
            instanceOf(
                TypedPropertyNode.class,
                has("name", x -> x.name(), equalTo("x")),
                has("type", x -> x.type(), has("value", x -> x.value(), equalTo(Types.STRING))),
                has("body", x -> x.body(), isSequence(
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
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

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
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.body(), isSequence(
            instanceOf(
                TypedPropertyNode.class,
                has("body", x -> x.body(), isSequence(
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
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.body(), isSequence(
            instanceOf(TypedPropertyNode.class, has("name", x -> x.name(), equalTo("x"))),
            instanceOf(
                TypedPropertyNode.class,
                has("name", x -> x.name(), equalTo("y")),
                has("body", x -> x.body(), isSequence(
                    isTypedReturnNode().withExpression(isTypedMemberReferenceNode().withName("x").withType(Types.STRING))
                ))
            )
        ));
    }

    @Test
    public void functionsAreIncludedInTypedNode() {
        // TODO: test functions more thoroughly?
        // Add equivalents to (or combine with) property tests? It relies on the same machinery.
        var untypedNode = UntypedRecordNode.builder("Example")
            .addBodyDeclaration(UntypedFunctionNode.builder()
                .name("x")
                .returnType(Untyped.typeLevelReference("String"))
                .addBodyStatement(Untyped.returnStatement(Untyped.string("hello")))
                .build()
            )
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(typedNode.body(), isSequence(
            isTypedFunctionNode()
                .withName("x")
                .withReturnType(Types.STRING)
                .withBody(isSequence(
                    isTypedReturnNode().withExpression(isTypedStringLiteralNode("hello"))
                ))
        ));
    }

    @Test
    public void functionsAreAddedToMemberTypes() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addBodyDeclaration(UntypedFunctionNode.builder()
                .name("x")
                .returnType(Untyped.typeLevelReference("String"))
                .addBodyStatement(Untyped.returnStatement(Untyped.string("hello")))
                .build()
            )
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedRecordNode) result.typedNode();
        assertThat(
            result.context().memberType(typedNode.type(), "x"),
            equalTo(Optional.of(Types.methodType(
                NamespaceId.source("a", "b"),
                List.of(),
                Types.STRING
            )))
        );
    }

    @Test
    public void whenRecordHasTwoMembersWithTheSameNameThenAnErrorIsThrown() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("Int")))
            .addField(Untyped.recordField("x", Untyped.typeLevelReference("Int")))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceId.source("a", "b"));

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
        var interfaceType = Types.sealedInterfaceType(NamespaceId.source("a", "b"), "Person");
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceId.source("a", "b"))
            .addLocal("Person", Types.metaType(interfaceType), NullSource.INSTANCE);

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.typedNode(), instanceOf(
            TypedRecordNode.class,
            has("supertypes", x -> x.supertypes(), isSequence(
                has("value", x -> x.value(), instanceOf(
                    Type.class,
                    isInterfaceType(NamespaceId.source("a", "b"), "Person"))
                )
            ))
        ));
        var recordType = ((TypedRecordNode) result.typedNode()).type();
        assertThat(result.context().subtypeRelations().extendedTypes(recordType), containsExactly(
            equalTo(interfaceType)
        ));
        assertThat(result.context().sealedInterfaceCases(interfaceType), containsExactly(
            isRecordType(NamespaceId.source("a", "b"), "User")
        ));
    }

    @Test
    public void whenSupertypeIsNotInterfaceThenErrorIsThrown() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.typeLevelReference("Bool"))
            .build();
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceId.source("a", "b"))
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
            .enterNamespace(NamespaceId.source("a", "b"))
            .addLocal("Person", Types.metaType(Types.sealedInterfaceType(NamespaceId.source("d", "e"), "Person")), NullSource.INSTANCE);

        assertThrows(
            CannotExtendSealedInterfaceFromDifferentNamespaceError.class,
            () -> typeCheckNamespaceStatementAllPhases(untypedNode, context)
        );
    }

    @Test
    public void whenSupertypeIsUnsealedInterfaceFromDifferentNamespaceThenInterfaceIsAddedAsSupertype() {
        var untypedNode = UntypedRecordNode.builder("User")
            .addSupertype(Untyped.typeLevelReference("Person"))
            .build();
        var interfaceType = Types.unsealedInterfaceType(NamespaceId.source("d", "e"), "Person");
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceId.source("a", "b"))
            .addLocal("Person", Types.metaType(interfaceType), NullSource.INSTANCE);

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.typedNode(), instanceOf(
            TypedRecordNode.class,
            has("supertypes", x -> x.supertypes(), isSequence(
                has("value", x -> x.value(), equalTo(interfaceType))
            ))
        ));
        var recordType = ((TypedRecordNode) result.typedNode()).type();
        assertThat(result.context().subtypeRelations().extendedTypes(recordType), containsExactly(
            equalTo(interfaceType)
        ));
        assertThat(result.context().sealedInterfaceCases(interfaceType), containsExactly());
    }

    private Matcher<Type> isInterfaceType(NamespaceId namespaceId, String name) {
        return instanceOf(
            InterfaceType.class,
            has("namespaceId", x -> x.namespaceId(), equalTo(namespaceId)),
            has("name", x -> x.name(), equalTo(name))
        );
    }

    private Matcher<Type> isRecordType(NamespaceId namespaceId, String name) {
        return instanceOf(
            RecordType.class,
            has("namespaceId", x -> x.namespaceId(), equalTo(namespaceId)),
            has("name", x -> x.name(), equalTo(name))
        );
    }
}
