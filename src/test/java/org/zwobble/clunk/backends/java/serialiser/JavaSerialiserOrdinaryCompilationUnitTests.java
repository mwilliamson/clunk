package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserOrdinaryCompilationUnitTests {
    @Test
    public void includesPackageAndDeclaration() {
        var node = new JavaOrdinaryCompilationUnitNode(
            "com.example",
            List.of(),
            JavaRecordDeclarationNode.builder("Example").build()
        );

        var result = serialiseToString(node, JavaSerialiser::serialiseOrdinaryCompilationUnit);

        assertThat(result, equalTo(
            """
            package com.example;
            
            public record Example() {
            }"""
        ));
    }

    @Test
    public void includesImports() {
        var node = new JavaOrdinaryCompilationUnitNode(
            "com.example",
            List.of(
                Java.importStatic("com.example.tests", "assertThat"),
                Java.importStatic("com.example.tests", "equalTo")
            ),
            JavaRecordDeclarationNode.builder("Example").build()
        );

        var result = serialiseToString(node, JavaSerialiser::serialiseOrdinaryCompilationUnit);

        assertThat(result, equalTo(
            """
            package com.example;
            
            import static com.example.tests.assertThat;
            import static com.example.tests.equalTo;
            
            public record Example() {
            }"""
        ));
    }
}
