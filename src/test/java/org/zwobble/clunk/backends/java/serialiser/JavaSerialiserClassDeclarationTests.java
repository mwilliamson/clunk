package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaClassDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaMethodDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
