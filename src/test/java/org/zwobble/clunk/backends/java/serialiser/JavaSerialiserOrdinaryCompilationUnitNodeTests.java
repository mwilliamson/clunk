package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.java.serialiser.JavaSerialiser.serialiseOrdinaryCompilationUnit;

public class JavaSerialiserOrdinaryCompilationUnitNodeTests {
    @Test
    public void includesPackageAndDeclaration() {
        var node = new JavaOrdinaryCompilationUnitNode(
            "com.example",
            JavaRecordDeclarationNode.builder("Example").build()
        );

        var builder = new CodeBuilder();
        serialiseOrdinaryCompilationUnit(node, builder);

        assertThat(builder.toString(), equalTo(
            """
            package com.example;
            
            public record Example() {
            }"""
        ));
    }
}