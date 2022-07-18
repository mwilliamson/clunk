package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.typechecker.FieldsLookup;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.typechecker.SubtypeRelation;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.NamespaceName;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToJavaRecord() {
        var node = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Example")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .build();
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example(String first, int second) {
                }"""
        ));
    }

    @Test
    public void whenRecordHasInterfaceAsSupertypeThenJavaRecordImplementsInterface() {
        var node = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Example").build();
        var subtypeLookup = SubtypeLookup.fromSubtypeRelations(List.of(
            new SubtypeRelation(node.type(), new InterfaceType(NamespaceName.fromParts("a", "b"), "X")),
            new SubtypeRelation(node.type(), new InterfaceType(NamespaceName.fromParts("a", "b"), "Y"))
        ));
        var context = new JavaCodeGeneratorContext(JavaTargetConfig.stub(), FieldsLookup.EMPTY, subtypeLookup);

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example() implements X, Y {
                }"""
        ));
    }
}
