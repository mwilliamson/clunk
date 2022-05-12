package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorNamespaceTests {
    @Test
    public void namespaceIsCompiledToTypeScriptModule() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.fromParts("example", "project"))
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

    @Test
    public void macroImportsDoNotImmediatelyGenerateImports() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.fromParts("example", "project"))
            .addImport(Typed.import_(
                NamespaceName.fromParts("stdlib", "assertions"), "assertThat",
                new StaticFunctionType(
                    NamespaceName.fromParts("stdlib", "assertions"),
                    "assertThat",
                    List.of(),
                    Types.UNIT
                )
            ))
            .build();

        var result = TypeScriptCodeGenerator.compileNamespace(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
        assertThat(string, equalTo(""));
    }

    @Test
    public void macrosGenerateImports() {
        var assertThatType = new StaticFunctionType(
            NamespaceName.fromParts("stdlib", "assertions"),
            "assertThat",
            List.of(),
            Types.UNIT
        );
        var equalToType = new StaticFunctionType(
            NamespaceName.fromParts("stdlib", "matchers"),
            "equalTo",
            List.of(),
            Types.UNIT
        );
        var node = TypedNamespaceNode
            .builder(NamespaceName.fromParts("example", "project"))
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
                        Typed.call(
                            Typed.reference("assertThat", assertThatType),
                            List.of(
                                Typed.intLiteral(1),
                                Typed.call(
                                    Typed.reference("equalTo", equalToType),
                                    List.of(Typed.intLiteral(2)),
                                    Types.UNIT
                                )
                            ),
                            Types.UNIT
                        )
                    ))
                    .build()
            )
            .build();

        var result = TypeScriptCodeGenerator.compileNamespace(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import {assertThat} from "@mwilliamson/precisely";
            
            
            import {equalTo} from "@mwilliamson/precisely";
            
            
            (test)("x", function () {
                (assertThat)(1, (equalTo)(2));
            });
            """));
    }
}
