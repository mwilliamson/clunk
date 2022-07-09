package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.typechecker.FieldsLookup;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.typechecker.SubtypeRelation;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToJavaRecord() {
        var node = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Example").build();
        var context = JavaCodeGeneratorContext.stub(new FieldsLookup(Map.ofEntries(
            Map.entry(node.type(), List.of(
                Typed.recordField("first", StringType.INSTANCE),
                Typed.recordField("second", IntType.INSTANCE)
            ))
        )));

        var result = JavaCodeGenerator.compileRecord(
            node,
            context
        );

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
        var fieldsLookup = new FieldsLookup(Map.of(node.type(), List.of()));
        var subtypeLookup = SubtypeLookup.fromSubtypeRelations(List.of(
            new SubtypeRelation(node.type(), new InterfaceType(NamespaceName.fromParts("a", "b"), "X")),
            new SubtypeRelation(node.type(), new InterfaceType(NamespaceName.fromParts("a", "b"), "Y"))
        ));
        var context = new JavaCodeGeneratorContext(JavaTargetConfig.stub(), fieldsLookup, subtypeLookup);

        var result = JavaCodeGenerator.compileRecord(
            node,
            context
        );

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example() implements a.b.X, a.b.Y {
                }"""
        ));
    }
}
