package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaRecordComponentNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.java.serialiser.JavaSerialiser.serialiseRecordDeclaration;

public class JavaSerialiserRecordDeclarationNodeTests {
    @Test
    public void emptyRecord() {
        var node = JavaRecordDeclarationNode.builder("Example").build();

        var stringBuilder = new StringBuilder();
        serialiseRecordDeclaration(node, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
            public record Example() {
            }"""
        ));
    }

    @Test
    public void recordWithOneComponent() {
        var node = JavaRecordDeclarationNode.builder("Example")
            .addComponent(new JavaRecordComponentNode(Java.typeReference("String"), "first"))
            .build();

        var stringBuilder = new StringBuilder();
        serialiseRecordDeclaration(node, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
            public record Example(String first) {
            }"""
        ));
    }

    @Test
    public void recordWithMultipleComponents() {
        var node = JavaRecordDeclarationNode.builder("Example")
            .addComponent(new JavaRecordComponentNode(Java.typeReference("String"), "first"))
            .addComponent(new JavaRecordComponentNode(Java.typeReference("int"), "second"))
            .build();

        var stringBuilder = new StringBuilder();
        serialiseRecordDeclaration(node, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
            public record Example(String first, int second) {
            }"""
        ));
    }
}
