package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaClassDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaMethodDeclarationNode;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserClassDeclarationTests {
    @Test
    public void canSerialiseEmptyClass() {
        var node = JavaClassDeclarationNode.builder().name("Util").build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public class Util {
            }"""
        ));
    }

    @Test
    public void canSerialiseClassWithAnnotations() {
        var node = JavaClassDeclarationNode.builder()
            .name("TestSuite")
            .addAnnotation(Java.annotation(Java.typeVariableReference("Test")))
            .addAnnotation(Java.annotation(Java.typeVariableReference("Skip")))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            @Test
            @Skip
            public class TestSuite {
            }"""
        ));
    }

    @Test
    public void canSerialiseClassWithMethods() {
        var node = JavaClassDeclarationNode.builder()
            .name("Util")
            .addBodyDeclaration(
                JavaMethodDeclarationNode.builder()
                    .name("f")
                    .returnType(Java.typeVariableReference("void"))
                    .build()
            )
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public class Util {
                public void f() {
                }
            }"""
        ));
    }
}
