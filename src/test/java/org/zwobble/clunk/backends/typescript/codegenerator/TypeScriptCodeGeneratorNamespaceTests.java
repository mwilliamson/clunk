package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.SubtypeRelations;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorNamespaceTests {
    @Test
    public void namespaceIsCompiledToTypeScriptModule() {
        var record1 = TypedRecordNode.builder("First").build();
        var record2 = TypedRecordNode.builder("Second").build();
        var node = TypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addStatement(record1)
            .addStatement(record2)
            .addFieldType("First", record1.type())
            .addFieldType("Second", record2.type())
            .build();

        var result = TypeScriptCodeGenerator.compileNamespace(node, SubtypeRelations.EMPTY);

        assertThat(result.path(), equalTo("example/project"));
        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
        assertThat(string, equalTo(
            """
                class First {
                }
                class Second {
                }
                export {First, Second};
                """
        ));
    }

    @Test
    public void fieldImportsAreCompiled() {
        var node = TypedNamespaceNode
            .builder(NamespaceId.source("example", "project"))
            .addImport(Typed.import_(NamespaceName.fromParts("a", "b"), "C", Types.INT))
            .addImport(Typed.import_(NamespaceName.fromParts("example", "sibling"), "D", Types.INT))
            .build();

        var result = TypeScriptCodeGenerator.compileNamespace(node, SubtypeRelations.EMPTY);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import {C} from "../a/b";
            import {D} from "./sibling";
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

        var result = TypeScriptCodeGenerator.compileNamespace(node, SubtypeRelations.EMPTY);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import * as a from "../a";
            import * as c from "../b/c";
            import * as f from "../d/e/f";
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

        var result = TypeScriptCodeGenerator.compileNamespace(node, SubtypeRelations.EMPTY);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
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

        var result = TypeScriptCodeGenerator.compileNamespace(node, SubtypeRelations.EMPTY);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseModule);
        assertThat(string, equalTo("""
            import {assertThat} from "@mwilliamson/precisely";
            import {deepEqualTo} from "@mwilliamson/precisely";
            test("x", function () {
                assertThat(1, deepEqualTo(2));
            });
            """));
    }
}
