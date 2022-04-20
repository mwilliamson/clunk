package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorNamespaceTests {
    @Test
    public void namespaceIsCompiledToPythonModule() {
        var node = TypedNamespaceNode.builder(List.of("example", "project"))
            .addStatement(TypedRecordNode.builder("First").build())
            .addStatement(TypedRecordNode.builder("Second").build())
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespace(node);

        assertThat(result.name(), equalTo("example.project"));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import dataclasses
            @((dataclasses).dataclass)(frozen=True)
            class First:
                pass
            @((dataclasses).dataclass)(frozen=True)
            class Second:
                pass
            """
        ));
    }
}
