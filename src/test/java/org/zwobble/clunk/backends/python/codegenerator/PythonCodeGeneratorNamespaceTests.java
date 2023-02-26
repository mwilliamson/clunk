package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorNamespaceTests {
    @Test
    public void namespaceIsCompiledToPythonModule() {
        var record1 = TypedRecordNode.builder("First").build();
        var record2 = TypedRecordNode.builder("Second").build();
        var node = TypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addStatement(record1)
            .addStatement(record2)
            .build();

        var result = PythonCodeGenerator.compileNamespace(node);

        assertThat(result.name(), equalTo(List.of("example", "project")));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import dataclasses
            @dataclasses.dataclass(frozen=True)
            class First:
                pass
            @dataclasses.dataclass(frozen=True)
            class Second:
                pass
            """
        ));
    }

    @Test
    public void fieldImportsAreCompiled() {
        var node = TypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addImport(Typed.import_(NamespaceName.fromParts("a", "b"), "C", Types.INT))
            .build();

        var result = PythonCodeGenerator.compileNamespace(node);

        assertThat(result.name(), equalTo(List.of("example", "project")));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            from a.b import C
            """
        ));
    }

    @Test
    public void namespaceImportsAreCompiled() {
        var node = TypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addImport(Typed.import_(NamespaceName.fromParts("a"), Types.INT))
            .addImport(Typed.import_(NamespaceName.fromParts("b", "c"), Types.INT))
            .addImport(Typed.import_(NamespaceName.fromParts("d", "e", "f"), Types.INT))
            .build();

        var result = PythonCodeGenerator.compileNamespace(node);

        assertThat(result.name(), equalTo(List.of("example", "project")));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import a
            from b import c
            from d.e import f
            """
        ));
    }

    @Test
    public void macroImportsDoNotImmediatelyGenerateImports() {
        var node = TypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addImport(Typed.import_(
                NamespaceName.fromParts("stdlib", "assertions"), "assertThat",
                Types.staticFunctionType(
                    NamespaceId.source("stdlib", "assertions"),
                    "assertThat",
                    List.of(),
                    Types.UNIT
                )
            ))
            .build();

        var result = PythonCodeGenerator.compileNamespace(node);

        assertThat(result.name(), equalTo(List.of("example", "project")));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo(""));
    }

    @Test
    public void macrosGenerateImports() {
        var assertThatType = Types.staticFunctionType(
            NamespaceId.source("stdlib", "assertions"),
            "assertThat",
            List.of(),
            Types.UNIT
        );
        var equalToType = Types.staticFunctionType(
            NamespaceId.source("stdlib", "matchers"),
            "equalTo",
            List.of(),
            Types.UNIT
        );
        var node = TypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addImport(Typed.import_(
                NamespaceName.fromParts("stdlib", "assertions"), "assertThat",
                assertThatType
            ))
            .addImport(Typed.import_(
                NamespaceName.fromParts("stdlib", "assertions"), "equalTo",
                equalToType
            ))
            .addStatement(
                TypedTestNode.builder()
                    .name("x")
                    .addBodyStatement(Typed.expressionStatement(
                        Typed.callStatic(
                            Typed.localReference("assertThat", assertThatType),
                            List.of(
                                Typed.intLiteral(1),
                                Typed.callStatic(
                                    Typed.localReference("equalTo", equalToType),
                                    List.of(Typed.intLiteral(2))
                                )
                            )
                        )
                    ))
                    .build()
            )
            .build();

        var result = PythonCodeGenerator.compileNamespace(node);

        assertThat(result.name(), equalTo(List.of("example", "project")));
        var string = serialiseToString(result, PythonSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            from precisely import assert_that
            from precisely import equal_to
            def test_x():
                assert_that(1, equal_to(2))
            """));
    }
}
