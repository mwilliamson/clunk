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
            .name("f")
            .returnType(Java.typeReference("void"))
            .build();

        var result = serialiseToString(node, JavaSerialiser::serialiseMethodDeclaration);

        assertThat(result, equalTo("""
            public void f() {
            }
            """
        ));
    }
}
