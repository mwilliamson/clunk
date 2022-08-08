package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaInterfaceMethodDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserInterfaceMethodDeclarationTests {
    @Test
    public void canSerialiseMethodWithNoParams() {
        var node = JavaInterfaceMethodDeclarationNode.builder()
            .returnType(Java.typeVariableReference("String"))
            .name("describe")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseInterfaceMemberDeclaration);

        assertThat(result, equalTo("""
            String describe();
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithOneParam() {
        var node = JavaInterfaceMethodDeclarationNode.builder()
            .returnType(Java.typeVariableReference("String"))
            .name("describe")
            .addParam(Java.param(Java.typeVariableReference("int"), "indentation"))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseInterfaceMemberDeclaration);

        assertThat(result, equalTo("""
            String describe(int indentation);
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithMultipleParam() {
        var node = JavaInterfaceMethodDeclarationNode.builder()
            .returnType(Java.typeVariableReference("String"))
            .name("describe")
            .addParam(Java.param(Java.typeVariableReference("int"), "indentation"))
            .addParam(Java.param(Java.typeVariableReference("int"), "lineLength"))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseInterfaceMemberDeclaration);

        assertThat(result, equalTo("""
            String describe(int indentation, int lineLength);
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithOneTypeParam() {
        var node = JavaInterfaceMethodDeclarationNode.builder()
            .addTypeParam("T")
            .returnType(Java.typeVariableReference("String"))
            .name("describe")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseInterfaceMemberDeclaration);

        assertThat(result, equalTo("""
            <T> String describe();
            """
        ));
    }

    @Test
    public void canSerialiseMethodWithMultipleTypeParams() {
        var node = JavaInterfaceMethodDeclarationNode.builder()
            .addTypeParam("T")
            .addTypeParam("U")
            .returnType(Java.typeVariableReference("String"))
            .name("describe")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseInterfaceMemberDeclaration);

        assertThat(result, equalTo("""
            <T, U> String describe();
            """
        ));
    }
}
