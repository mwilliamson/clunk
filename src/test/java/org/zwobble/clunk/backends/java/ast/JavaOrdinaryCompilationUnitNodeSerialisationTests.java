package org.zwobble.clunk.backends.java.ast;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.java.ast.JavaSerialiser.serialiseOrdinaryCompilationUnit;

public class JavaOrdinaryCompilationUnitNodeSerialisationTests {
    @Test
    public void includesPackageAndDeclaration() {
        var node = new JavaOrdinaryCompilationUnitNode(
            "com.example",
            new JavaRecordDeclarationNode(
                "Example"
            )
        );

        var stringBuilder = new StringBuilder();
        serialiseOrdinaryCompilationUnit(node, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
            package com.example;
            
            public record Example() {
            }"""
        ));
    }
}
