package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserInterfaceDeclarationTests {

    @Test
    public void emptyInterface() {
        var node = TypeScriptInterfaceDeclarationNode.builder("Example").build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            interface Example {
            }
            """
        ));
    }

    @Test
    public void interfaceWithOneComponent() {
        var node = TypeScriptInterfaceDeclarationNode.builder("Example")
            .addField(TypeScript.interfaceField("first", TypeScript.reference("string")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            interface Example {
                readonly first: string;
            }
            """
        ));
    }

    @Test
    public void interfaceWithMultipleComponents() {
        var node = TypeScriptInterfaceDeclarationNode.builder("Example")
            .addField(TypeScript.interfaceField("first", TypeScript.reference("string")))
            .addField(TypeScript.interfaceField("second", TypeScript.reference("number")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            interface Example {
                readonly first: string;
                readonly second: number;
            }
            """
        ));
    }
}
