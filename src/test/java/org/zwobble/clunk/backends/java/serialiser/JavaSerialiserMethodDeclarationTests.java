package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaMethodDeclarationNode;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserMethodDeclarationTests {
    @Test
    public void canSerialiseEmptyMethod() {
        var node = JavaMethodDeclarationNode.builder()
            .returnType(Java.typeVariableReference("void"))
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
            .returnType(Java.typeVariableReference("void"))
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
            .returnType(Java.typeVariableReference("void"))
            .name("f")
            .addParam(Java.param(Java.typeVariableReference("int"), "x"))
            .addParam(Java.param(Java.typeVariableReference("String"), "y"))
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
            .returnType(Java.typeVariableReference("void"))
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
            .returnType(Java.typeVariableReference("void"))
            .name("f")
            .addAnnotation(Java.annotation(Java.typeVariableReference("Test")))
            .addAnnotation(Java.annotation(Java.typeVariableReference("Skip")))
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

    @Test
    public void canSerialiseMethodWithOneTypeParam() {
        var node = JavaMethodDeclarationNode.builder()
            .addTypeParam("T")
            .returnType(Java.typeVariableReference("void"))
            .name("f")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            public <T> void f() {
            }
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithMultipleTypeParams() {
        var node = JavaMethodDeclarationNode.builder()
            .addTypeParam("T")
            .addTypeParam("U")
            .returnType(Java.typeVariableReference("void"))
            .name("f")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            public <T, U> void f() {
            }
            """
        ));
    }
}
