package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.JavaInterfaceDeclarationNode;

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
}
