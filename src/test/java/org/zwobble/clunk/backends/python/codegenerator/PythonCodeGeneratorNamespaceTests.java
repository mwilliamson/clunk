package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.NamespaceName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorNamespaceTests {
    @Test
    public void namespaceIsCompiledToPythonModule() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.parts("example", "project"))
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

    @Test
    public void fieldImportsAreCompiled() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.parts("example", "project"))
            .addImport(Typed.import_(NamespaceName.parts("a", "b"), "C"))
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespace(node);

        assertThat(result.name(), equalTo("example.project"));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import dataclasses
            from a.b import C
            """
        ));
    }

    @Test
    public void namespaceImportsAreCompiled() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.parts("example", "project"))
            .addImport(Typed.import_(NamespaceName.parts("a")))
            .addImport(Typed.import_(NamespaceName.parts("b", "c")))
            .addImport(Typed.import_(NamespaceName.parts("d", "e", "f")))
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespace(node);

        assertThat(result.name(), equalTo("example.project"));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import dataclasses
            import a
            from b import c
            from d.e import f
            """
        ));
    }
}
