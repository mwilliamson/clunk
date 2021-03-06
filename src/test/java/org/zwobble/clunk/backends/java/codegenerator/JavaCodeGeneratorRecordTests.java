package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
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
    public void propertiesAreCompiledToMethods() {
        var node = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Example")
            .addProperty(Typed.property(
                "value",
                Typed.typeLevelString(),
                List.of(Typed.returnStatement(Typed.string("hello")))
            ))
            .build();
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example() {
                    public String value() {
                        return "hello";
                    }
                }"""
        ));
    }

    @Test
    public void whenRecordHasInterfaceAsSupertypeThenJavaRecordImplementsInterface() {
        var node = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Example")
            .addSupertype(Typed.typeLevelReference("X", new InterfaceType(NamespaceName.fromParts("a", "b"), "X")))
            .addSupertype(Typed.typeLevelReference("Y", new InterfaceType(NamespaceName.fromParts("a", "b"), "Y")))
            .build();
        var context = JavaCodeGeneratorContext.stub();

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
