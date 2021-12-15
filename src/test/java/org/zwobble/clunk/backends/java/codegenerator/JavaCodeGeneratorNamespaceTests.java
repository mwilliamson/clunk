package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorNamespaceTests {
    @Test
    public void recordsInNamespaceAreCompiledToSeparateJavaCompilationUnits() {
        var node = TypedNamespaceNode.builder(List.of("example", "project"))
            .addStatement(TypedRecordNode.builder("First").build())
            .addStatement(TypedRecordNode.builder("Second").build())
            .build();

        var result = JavaCodeGenerator.compileNamespace(node);

        var strings = result
            .stream()
            .map(compilationUnit -> serialiseToString(
                compilationUnit,
                JavaSerialiser::serialiseOrdinaryCompilationUnit)
            )
            .collect(Collectors.toList());
        assertThat(strings, contains(
            equalTo(
                """
                    package example.project; 
                    
                    public record First() {
                    }"""
            ),
            equalTo(
                """
                    package example.project;
                    
                    public record Second() {
                    }"""
            )
        ));
    }

    @Test
    public void recordIsCompiledToJavaRecord() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", StringType.INSTANCE))
            .addField(Typed.recordField("second", IntType.INSTANCE))
            .build();

        var result = JavaCodeGenerator.compileRecord(node);
        var builder = new CodeBuilder();
        JavaSerialiser.serialiseTypeDeclaration(result, builder);

        assertThat(builder.toString(), equalTo(
            """
                public record Example(String first, int second) {
                }"""
        ));
    }
}
