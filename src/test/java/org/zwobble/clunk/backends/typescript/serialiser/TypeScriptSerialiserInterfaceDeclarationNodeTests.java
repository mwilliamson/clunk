package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser.serialiseInterfaceDeclaration;

public class TypeScriptSerialiserInterfaceDeclarationNodeTests {

    @Test
    public void emptyInterface() {
        var node = TypeScriptInterfaceDeclarationNode.builder("Example").build();

        var builder = new CodeBuilder();
        serialiseInterfaceDeclaration(node, builder);

        assertThat(builder.toString(), equalTo(
            """
            interface Example {
            }"""
        ));
    }

    @Test
    public void interfaceWithOneComponent() {
        var node = TypeScriptInterfaceDeclarationNode.builder("Example")
            .addField(TypeScript.interfaceField("first", TypeScript.reference("string")))
            .build();

        var builder = new CodeBuilder();
        serialiseInterfaceDeclaration(node, builder);

        assertThat(builder.toString(), equalTo(
            """
            interface Example {
                first: string;
            }"""
        ));
    }

    @Test
    public void interfaceWithMultipleComponents() {
        var node = TypeScriptInterfaceDeclarationNode.builder("Example")
            .addField(TypeScript.interfaceField("first", TypeScript.reference("string")))
            .addField(TypeScript.interfaceField("second", TypeScript.reference("number")))
            .build();

        var builder = new CodeBuilder();
        serialiseInterfaceDeclaration(node, builder);

        assertThat(builder.toString(), equalTo(
            """
            interface Example {
                first: string;
                second: number;
            }"""
        ));
    }
}