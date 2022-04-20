package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaMethodDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserMethodDeclarationTests {
    @Test
    public void canSerialiseEmptyMethod() {
        var node = JavaMethodDeclarationNode.builder()
            .returnType(Java.typeReference("void"))
            .name("f")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            public void f() {
            }
            """
        ));
    }

    @Test
    public void canSerialiseStaticMethod() {
        var node = JavaMethodDeclarationNode.builder()
            .returnType(Java.typeReference("void"))
            .name("f")
            .isStatic(true)
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            public static void f() {
            }
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithParams() {
        var node = JavaMethodDeclarationNode.builder()
            .returnType(Java.typeReference("void"))
            .name("f")
            .addParam(Java.param(Java.typeReference("int"), "x"))
            .addParam(Java.param(Java.typeReference("String"), "y"))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            public void f(int x, String y) {
            }
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithBody() {
        var node = JavaMethodDeclarationNode.builder()
            .returnType(Java.typeReference("void"))
            .name("f")
            .addBodyStatement(Java.returnStatement(Java.boolFalse()))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            public void f() {
                return false;
            }
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithAnnotations() {
        var node = JavaMethodDeclarationNode.builder()
            .returnType(Java.typeReference("void"))
            .name("f")
            .addAnnotation(Java.annotation(Java.typeReference("Test")))
            .addAnnotation(Java.annotation(Java.typeReference("Skip")))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            @Test
            @Skip
            public void f() {
            }
            """
        ));
    }
}
