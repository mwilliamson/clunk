package org.zwobble.clunk.backends.java;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedStaticExpressionNode;
import org.zwobble.clunk.backends.java.ast.JavaSerialiser;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class JavaCodeGeneratorTests {
    @Test
    public void recordsInNamespaceAreCompiledToSeparateJavaCompilationUnits() {
        var node = TypedNamespaceNode.builder(List.of("example", "project"))
            .addStatement(TypedRecordNode.builder("First").build())
            .addStatement(TypedRecordNode.builder("Second").build())
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
        var node = TypedRecordNode.builder("Example")
            .addField(new TypedRecordFieldNode("first", new TypedStaticExpressionNode(StringType.INSTANCE, NullSource.INSTANCE), NullSource.INSTANCE))
            .addField(new TypedRecordFieldNode("second", new TypedStaticExpressionNode(IntType.INSTANCE, NullSource.INSTANCE), NullSource.INSTANCE))
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
