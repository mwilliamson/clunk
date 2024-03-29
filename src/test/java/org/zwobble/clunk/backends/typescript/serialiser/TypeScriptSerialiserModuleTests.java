package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserModuleTests {
    @Test
    public void includesStatementsInModule() {
        var node = TypeScript.module(
            List.of("example", "project"),
            List.of(
                TypeScriptInterfaceDeclarationNode.builder("First").build(),
                TypeScriptInterfaceDeclarationNode.builder("Second").build()
            )
        );

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseModule);

        assertThat(result, equalTo("""
            interface First {
            }
            interface Second {
            }
            """));
    }
}
