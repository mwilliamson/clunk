package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaInterfaceDeclarationNode;

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
}
