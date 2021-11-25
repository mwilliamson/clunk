package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorNamespaceTests {
    @Test
    public void namespaceIsCompiledToTypeScriptModule() {
        var node = TypedNamespaceNode.builder(List.of("example", "project"))
            .addStatement(TypedRecordNode.builder("First").build())
            .addStatement(TypedRecordNode.builder("Second").build())
            .build();

        var result = TypeScriptCodeGenerator.compileNamespace(node);

        assertThat(result.path(), equalTo("example/project"));
        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
        assertThat(string, equalTo(
            """
                interface First {
                }
                
                interface Second {
                }"""
        ));
    }
}
