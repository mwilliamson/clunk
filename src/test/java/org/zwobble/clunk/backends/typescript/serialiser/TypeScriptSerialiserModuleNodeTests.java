package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser.serialiseModule;

public class TypeScriptSerialiserModuleNodeTests {
    @Test
    public void includesStatementsInModule() {
        var node = TypeScript.module(
            "example/project",
            List.of(
                TypeScriptInterfaceDeclarationNode.builder("First").build(),
                TypeScriptInterfaceDeclarationNode.builder("Second").build()
            )
        );

        var builder = new CodeBuilder();
        serialiseModule(node, builder);

        assertThat(builder.toString(), equalTo("""
            interface First {
            }
            
            interface Second {
            }"""));
    }
}
