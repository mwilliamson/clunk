package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserOrdinaryCompilationUnitTests {
    @Test
    public void includesPackageAndDeclaration() {
        var node = new JavaOrdinaryCompilationUnitNode(
            "com.example",
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
}
