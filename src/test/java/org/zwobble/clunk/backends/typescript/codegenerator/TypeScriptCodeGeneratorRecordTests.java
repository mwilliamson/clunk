package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.typechecker.FieldsLookup;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.typechecker.SubtypeRelation;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.NamespaceName;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToTypeScriptInterface() {
        var node = TypedRecordNode.builder("Example").build();
        var context = TypeScriptCodeGeneratorContext.stub(new FieldsLookup(Map.ofEntries(
            Map.entry(node.type(), List.of(
                Typed.recordField("first", Typed.typeLevelString()),
                Typed.recordField("second", Typed.typeLevelInt())
            ))
        )));

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                interface Example {
                    readonly first: string;
                    readonly second: number;
                }
                """
        ));
    }

    @Test
    public void whenRecordIsSubtypeOfSealedInterfaceThenTypePropertyIsDiscriminator() {
        var node = TypedRecordNode.builder("Example").build();
        var fieldsLookup = new FieldsLookup(Map.ofEntries(
            Map.entry(node.type(), List.of(
                Typed.recordField("first", Typed.typeLevelString()),
                Typed.recordField("second", Typed.typeLevelInt())
            ))
        ));
        var subtypeLookup = SubtypeLookup.fromSubtypeRelations(List.of(
            new SubtypeRelation(node.type(), new InterfaceType(NamespaceName.fromParts(), "Supertype"))
        ));
        var context = new TypeScriptCodeGeneratorContext(fieldsLookup, subtypeLookup);

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                interface Example {
                    readonly type: "Example";
                    readonly first: string;
                    readonly second: number;
                }
                """
        ));
    }
}
