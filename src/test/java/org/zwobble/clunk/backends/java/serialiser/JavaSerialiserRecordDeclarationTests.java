package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserRecordDeclarationTests {
    @Test
    public void emptyRecord() {
        var node = JavaRecordDeclarationNode.builder("Example").build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo(
            """
            public record Example() {
            }"""
        ));
    }

    @Test
    public void recordWithOneComponent() {
        var node = JavaRecordDeclarationNode.builder("Example")
            .addComponent(new JavaRecordComponentNode(Java.typeVariableReference("String"), "first"))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo(
            """
            public record Example(String first) {
            }"""
        ));
    }

    @Test
    public void recordWithMultipleComponents() {
        var node = JavaRecordDeclarationNode.builder("Example")
            .addComponent(new JavaRecordComponentNode(Java.typeVariableReference("String"), "first"))
            .addComponent(new JavaRecordComponentNode(Java.typeVariableReference("int"), "second"))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo(
            """
            public record Example(String first, int second) {
            }"""
        ));
    }

    @Test
    public void recordImplementingOtherTypes() {
        var node = JavaRecordDeclarationNode.builder("Example")
            .addImplements(Java.typeVariableReference("A"))
            .addImplements(Java.typeVariableReference("B"))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo(
            """
            public record Example() implements A, B {
            }"""
        ));
    }

    @Test
    public void canSerialiseRecordWithMethods() {
        var node = JavaRecordDeclarationNode.builder("Example")
            .addBodyDeclaration(
                JavaMethodDeclarationNode.builder()
                    .name("f")
                    .returnType(Java.typeVariableReference("void"))
                    .build()
            )
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public record Example() {
                public void f() {
                }
            }"""
        ));
    }
}
