package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

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
            .addImport(Typed.import_(NamespaceName.parts("a", "b"), "C", Types.INT))
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespace(node);

        assertThat(result.name(), equalTo("example.project"));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            from a.b import C
            """
        ));
    }

    @Test
    public void namespaceImportsAreCompiled() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.parts("example", "project"))
            .addImport(Typed.import_(NamespaceName.parts("a"), Types.INT))
            .addImport(Typed.import_(NamespaceName.parts("b", "c"), Types.INT))
            .addImport(Typed.import_(NamespaceName.parts("d", "e", "f"), Types.INT))
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespace(node);

        assertThat(result.name(), equalTo("example.project"));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import a
            from b import c
            from d.e import f
            """
        ));
    }
}
