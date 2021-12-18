package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
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
    public void functionsAreGroupedIntoSingleClassNamedAfterNamespace() {
        var node = TypedNamespaceNode.builder(List.of("example", "project"))
            .addStatement(TypedFunctionNode.builder().name("f").returnType(StringType.INSTANCE).build())
            .addStatement(TypedFunctionNode.builder().name("g").returnType(StringType.INSTANCE).build())
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
                    
                    public class Project {
                        public static String f() {
                        }
                        public static String g() {
                        }
                    }"""
            )
        ));
    }
}
