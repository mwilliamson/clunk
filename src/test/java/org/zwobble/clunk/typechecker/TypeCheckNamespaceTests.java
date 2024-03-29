package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedFunctionNode;
import org.zwobble.clunk.ast.untyped.UntypedNamespaceNode;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.Map;
import java.util.Optional;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedImportNode;
import static org.zwobble.clunk.ast.typed.TypedNodeMatchers.isTypedRecordNode;

public class TypeCheckNamespaceTests {
    @Test
    public void namespaceIsTypeChecked() {
        var untypedNode = UntypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addStatement(UntypedRecordNode.builder("X").build())
            .build();

        var result = TypeChecker.typeCheckNamespace(untypedNode, TypeCheckerContext.stub());

        assertThat(result.typedNode(), allOf(
            has("id", x -> x.id(), equalTo(NamespaceId.source("example", "project"))),
            has("statements", x -> x.statements(), isSequence(
                isTypedRecordNode(has("name", x -> x.name(), equalTo("X")))
            ))
        ));
    }

    @Test
    public void namespaceTypeIsUpdatedInContext() {
        var namespaceId = NamespaceId.source("example", "project");
        var untypedNode = UntypedNamespaceNode
            .builder(namespaceId)
            .addStatement(UntypedRecordNode.builder("X").build())
            .build();

        var result = TypeChecker.typeCheckNamespace(untypedNode, TypeCheckerContext.stub());

        assertThat(
            result.context().typeOfNamespace(namespaceId),
            equalTo(Optional.of(new NamespaceType(
                namespaceId,
                Map.of("X", Types.metaType(Types.recordType(namespaceId, "X")))
            )))
        );
        assertThat(
            result.context().memberType(result.context().typeOfNamespace(namespaceId).get(), "X"),
            equalTo(Optional.of(Types.metaType(Types.recordType(namespaceId, "X"))))
        );
    }

    @Test
    public void importedFieldIsAddedToEnvironment() {
        var untypedNode = UntypedNamespaceNode.builder(NamespaceId.source("example", "project"))
            .addImport(Untyped.import_(NamespaceName.fromParts("x", "y"), "IntAlias"))
            .addStatement(
                UntypedRecordNode.builder("X")
                    .addField(Untyped.recordField("f", Untyped.typeLevelReference("IntAlias"))).build()
            )
            .build();
        var namespaceType = new NamespaceType(
            NamespaceId.source("x", "y"),
            Map.of("IntAlias", Types.metaType(Types.INT))
        );
        var context = TypeCheckerContext.stub()
            .updateNamespaceType(namespaceType);

        var result = TypeChecker.typeCheckNamespace(untypedNode, context);

        assertThat(result.typedNode(), allOf(
            has("imports", x -> x.imports(), isSequence(
                isTypedImportNode(allOf(
                    has("namespaceName", x -> x.namespaceName(), equalTo(NamespaceName.fromParts("x", "y"))),
                    has("fieldName", x -> x.fieldName(), equalTo(Optional.of("IntAlias")))
                ))
            ))
        ));
        var typedRecordNode = (TypedRecordNode) result.typedNode().statements().get(0);
        assertThat(result.context().constructorType(typedRecordNode.type()), isOptionalOf(instanceOf(
            ConstructorType.class,
            has("positionalParams", x -> x.positionalParams(), isSequence(equalTo(Types.INT)))
        )));
    }

    @Test
    public void cannotDefineMultipleTypesWithSameName() {
        var untypedNode = UntypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addStatement(UntypedRecordNode.builder("X").build())
            .addStatement(UntypedRecordNode.builder("X").build())
            .build();

        var result = assertThrows(VariableAlreadyDefinedError.class, () -> TypeChecker.typeCheckNamespace(untypedNode, TypeCheckerContext.stub()));

        assertThat(result.variableName(), equalTo("X"));
    }

    @Test
    public void whenVariableShadowsBuiltinThenEarlierReferencesUsesVariable() {
        var untypedNode = UntypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addStatement(UntypedFunctionNode.builder().addPositionalParam(Untyped.param("x", Untyped.typeLevelReference("X"))).build())
            .addStatement(UntypedRecordNode.builder("X").build())
            .build();
        var context = TypeCheckerContext.stub()
            .addLocal("X", Types.metaType(Types.INT), NullSource.INSTANCE);

        var result = TypeChecker.typeCheckNamespace(untypedNode, context);

        var typedFunctionNode = (TypedFunctionNode) result.typedNode().statements().get(0);
        var typedRecordNode = (TypedRecordNode) result.typedNode().statements().get(1);
        assertThat(typedFunctionNode.params().positional().get(0).type().value(), equalTo(typedRecordNode.type()));
    }

    @Test
    public void returnedContextLeavesBodyEnvironment() {
        var untypedNode = UntypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addStatement(UntypedRecordNode.builder("X").build())
            .build();
        var context = TypeCheckerContext.stub();

        var result = TypeChecker.typeCheckNamespace(untypedNode, context);

        assertThat(result.context().currentFrame().environment().containsKey("X"), equalTo(false));
    }
}
