package org.zwobble.clunk.typechecker;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.untyped.Untyped;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.types.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasRecordComponentWithValue.has;

public class TypeCheckRecordTests {
    @Test
    public void recordIsTypeChecked() {
        var untypedNode = UntypedRecordNode.builder("Example")
            .addField(Untyped.recordField("x", Untyped.staticReference("String")))
            .build();

        var result = TypeChecker.typeCheckNamespaceStatement(
            untypedNode,
            TypeCheckerContext.stub().enterNamespace(NamespaceName.fromParts("a", "b"))
        );

        assertThat(result.typedNode(), allOf(
            has("name", equalTo("Example")),
            has("fields", contains(
                allOf(
                    has("name", equalTo("x")),
                    has("type", has("type", equalTo(StringType.INSTANCE)))
                )
            )),
            has("type", isRecordType(NamespaceName.fromParts("a", "b"), "Example"))
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
                .updateType("Person", Types.metaType(Types.interfaceType(NamespaceName.fromParts("a", "b"), "Person")))
        );

        assertThat(result.context().subtypeRelations(), containsInAnyOrder(
            allOf(
                has("subtype", isRecordType(NamespaceName.fromParts("a", "b"), "User")),
                has("supertype", isInterfaceType(NamespaceName.fromParts("a", "b"), "Person"))
            )
        ));
    }

    private Matcher<?> isInterfaceType(NamespaceName namespaceName, String name) {
        return cast(InterfaceType.class, has("namespaceName", equalTo(namespaceName)), has("name", equalTo(name)));
    }

    private Matcher<?> isRecordType(NamespaceName namespaceName, String name) {
        return cast(RecordType.class, has("namespaceName", equalTo(namespaceName)), has("name", equalTo(name)));
    }
}
