package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaInterfaceDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaInterfaceMethodDeclarationNode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserInterfaceDeclarationTests {
    @Test
    public void canSerialiseEmptyInterface() {
        var node = JavaInterfaceDeclarationNode.builder().name("Person").build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public interface Person {
            }
            """
        ));
    }

    @Test
    public void canSerialiseSealedInterface() {
        var node = JavaInterfaceDeclarationNode.builder()
            .name("Person")
            .sealed(List.of(Java.typeVariableReference("Author"), Java.typeVariableReference("Editor")))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public sealed interface Person permits Author, Editor {
            }
            """
        ));
    }

    @Test
    public void canSerialiseInterfaceWithBody() {
        var node = JavaInterfaceDeclarationNode.builder().name("Person")
            .addMemberDeclaration(
                JavaInterfaceMethodDeclarationNode.builder()
                    .returnType(Java.typeVariableReference("String"))
                    .name("describe")
                    .addParam(Java.param(Java.typeVariableReference("int"), "indentation"))
                    .build()
            )
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public interface Person {
                String describe(int indentation);
            }
            """
        ));
    }

    @Test
    public void canSerialiseInterfaceWithOneTypeParam() {
        var node = JavaInterfaceDeclarationNode.builder().name("Person")
            .addTypeParam("T")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public interface Person<T> {
            }
            """
        ));
    }

    @Test
    public void canSerialiseInterfaceWithMultipleTypeParams() {
        var node = JavaInterfaceDeclarationNode.builder().name("Person")
            .addTypeParam("T")
            .addTypeParam("U")
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeDeclaration);

        assertThat(result, equalTo("""
            public interface Person<T, U> {
            }
            """
        ));
    }
}
