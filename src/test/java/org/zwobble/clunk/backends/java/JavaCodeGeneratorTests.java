package org.zwobble.clunk.backends.java;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.NamespaceNode;
import org.zwobble.clunk.ast.RecordFieldNode;
import org.zwobble.clunk.ast.RecordNode;
import org.zwobble.clunk.ast.StaticReferenceNode;
import org.zwobble.clunk.backends.java.ast.JavaSerialiser;
import org.zwobble.clunk.sources.NullSource;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class JavaCodeGeneratorTests {
    @Test
    public void recordsInNamespaceAreCompiledToSeparateJavaCompilationUnits() {
        var node = NamespaceNode.builder(List.of("example", "project"))
            .addStatement(RecordNode.builder("First").build())
            .addStatement(RecordNode.builder("Second").build())
            .build();

        var result = JavaCodeGenerator.compileNamespace(node)
            .stream()
            .map(compilationUnit -> {
                var stringBuilder = new StringBuilder();
                JavaSerialiser.serialiseOrdinaryCompilationUnit(compilationUnit, stringBuilder);
                return stringBuilder.toString();
            })
            .collect(Collectors.toList());

        assertThat(result, contains(
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
        var node = RecordNode.builder("Example")
            .addField(new RecordFieldNode("first", new StaticReferenceNode("String", NullSource.INSTANCE), NullSource.INSTANCE))
            .addField(new RecordFieldNode("second", new StaticReferenceNode("Int", NullSource.INSTANCE), NullSource.INSTANCE))
            .build();

        var result = JavaCodeGenerator.compileRecord(node);
        var stringBuilder = new StringBuilder();
        JavaSerialiser.serialiseRecordDeclaration(result, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
                public record Example(String first, int second) {
                }"""
        ));
    }
}
