package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaRecordComponentNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserRecordDeclarationNodeTests {
    @Test
    public void emptyRecord() {
        var node = JavaRecordDeclarationNode.builder("Example").build();

        var result = serialiseToString(node, JavaSerialiser::serialiseRecordDeclaration);

        assertThat(result, equalTo(
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

        var result = serialiseToString(node, JavaSerialiser::serialiseRecordDeclaration);

        assertThat(result, equalTo(
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

        var result = serialiseToString(node, JavaSerialiser::serialiseRecordDeclaration);

        assertThat(result, equalTo(
            """
            public record Example(String first, int second) {
            }"""
        ));
    }
}
