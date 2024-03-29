package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedFunctionSignatureNode;
import org.zwobble.clunk.ast.typed.TypedInterfaceNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedFunctionSignatureNode;
import org.zwobble.clunk.ast.untyped.UntypedInterfaceNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.TypeLevelValueType;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Optional;

import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedTypeLevelExpressionNode;
import static org.zwobble.clunk.matchers.MapEntryMatcher.isMapEntry;
import static org.zwobble.clunk.matchers.TypeMatchers.isMetaType;
import static org.zwobble.clunk.typechecker.TypeCheckNamespaceStatementTesting.typeCheckNamespaceStatementAllPhases;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;

public class TypeCheckInterfaceTests {
    @Test
    public void interfaceTypeIsAddedToEnvironment() {
        var untypedNode = Untyped.interface_("DocumentElement");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.context().typeOf("DocumentElement", NullSource.INSTANCE),
            instanceOf(
                TypeLevelValueType.class,
                has("value", x -> x.value(), instanceOf(
                    InterfaceType.class,
                    has("namespaceId", x -> x.namespaceId(), equalTo(NamespaceId.source("a", "b"))),
                    has("name", x -> x.name(), equalTo("DocumentElement"))
                ))
            )
        );
    }

    @Test
    public void canTypeCheckEmptyUnsealedInterface() {
        var untypedNode = Untyped.interfaceUnsealed("DocumentElement");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.typedNode(), instanceOf(
            TypedInterfaceNode.class,
            has("name", x -> x.name(), equalTo("DocumentElement")),
            has("type", x -> x.type(), instanceOf(
                InterfaceType.class,
                has("namespaceId", x -> x.namespaceId(), equalTo(NamespaceId.source("a", "b"))),
                has("name", x -> x.name(), equalTo("DocumentElement")),
                has("isSealed", x -> x.isSealed(), equalTo(false))
            ))
        ));
    }

    @Test
    public void canTypeCheckEmptySealedInterface() {
        var untypedNode = Untyped.interfaceSealed("DocumentElement");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(result.typedNode(), instanceOf(
            TypedInterfaceNode.class,
            has("name", x -> x.name(), equalTo("DocumentElement")),
            has("type", x -> x.type(), instanceOf(
                InterfaceType.class,
                has("namespaceId", x -> x.namespaceId(), equalTo(NamespaceId.source("a", "b"))),
                has("name", x -> x.name(), equalTo("DocumentElement")),
                has("isSealed", x -> x.isSealed(), equalTo(true))
            ))
        ));
    }

    @Test
    public void addsNamespaceField() {
        var untypedNode = Untyped.interface_("DocumentElement");
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        assertThat(
            result.fieldType(),
            isOptionalOf(isMapEntry(
                equalTo("DocumentElement"),
                isMetaType(instanceOf(
                    InterfaceType.class,
                    has("name", x -> x.name(), equalTo("DocumentElement"))
                ))
            ))
        );
    }

    @Test
    public void functionsAreIncludedInTypedNode() {
        var untypedNode = UntypedInterfaceNode.builder("Example")
            .addBodyDeclaration(UntypedFunctionSignatureNode.builder()
                .name("x")
                .returnType(Untyped.typeLevelReference("String"))
                .build()
            )
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedInterfaceNode) result.typedNode();
        assertThat(typedNode.body(), isSequence(
            instanceOf(
                TypedFunctionSignatureNode.class,
                has("name", TypedFunctionSignatureNode::name, equalTo("x")),
                has(
                    "returnType",
                    TypedFunctionSignatureNode::returnType,
                    isTypedTypeLevelExpressionNode(Types.STRING)
                )
            )
        ));
    }

    @Test
    public void functionsAreAddedToMemberTypes() {
        var untypedNode = UntypedInterfaceNode.builder("Example")
            .addBodyDeclaration(UntypedFunctionSignatureNode.builder()
                .name("x")
                .returnType(Untyped.typeLevelReference("String"))
                .build()
            )
            .build();
        var context = TypeCheckerContext.stub().enterNamespace(NamespaceId.source("a", "b"));

        var result = typeCheckNamespaceStatementAllPhases(untypedNode, context);

        var typedNode = (TypedInterfaceNode) result.typedNode();
        assertThat(
            result.context().memberType(typedNode.type(), "x"),
            equalTo(Optional.of(Types.methodType(
                NamespaceId.source("a", "b"),
                List.of(),
                Types.STRING
            )))
        );
    }
}
