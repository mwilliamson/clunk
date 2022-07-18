package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorNamespaceTests {
    @Test
    public void recordsInNamespaceAreCompiledToSeparateJavaCompilationUnits() {
        var record1 = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "First").build();
        var record2 = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Second").build();
        var node = TypedNamespaceNode
            .builder(NamespaceName.fromParts("example", "project"))
            .addStatement(record1)
            .addStatement(record2)
            .build();

        var result = JavaCodeGenerator.compileNamespace(node, JavaTargetConfig.stub(), SubtypeLookup.EMPTY);

        assertThat(serialise(result), contains(
            equalTo(
                """
                    package example.project;
                    
                    public record First() {
                    }"""
            ),
            equalTo(
                """
                    package example.project;
                    
                    public record Second() {
                    }"""
            )
        ));
    }

    @Test
    public void functionsAreGroupedIntoSingleClassNamedAfterNamespace() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.fromParts("example", "project"))
            .addStatement(TypedFunctionNode.builder().name("f").returnType(Typed.typeLevelString()).build())
            .addStatement(TypedFunctionNode.builder().name("g").returnType(Typed.typeLevelString()).build())
            .build();

        var result = JavaCodeGenerator.compileNamespace(node, JavaTargetConfig.stub(), SubtypeLookup.EMPTY);

        assertThat(serialise(result), contains(
            equalTo(
                """
                    package example.project;
                    
                    public class Project {
                        public static String f() {
                        }
                        public static String g() {
                        }
                    }"""
            )
        ));
    }

    @Test
    public void testsAreGroupedIntoSingleClassNamedAfterNamespace() {
        var node = TypedNamespaceNode
            .builder(NamespaceName.fromParts("example", "project"))
            .addStatement(TypedTestNode.builder().name("f").build())
            .addStatement(TypedTestNode.builder().name("g").build())
            .build();

        var result = JavaCodeGenerator.compileNamespace(node, JavaTargetConfig.stub(), SubtypeLookup.EMPTY);

        assertThat(serialise(result), contains(
            equalTo(
                """
                    package example.project;

                    public class Project {
                        @org.junit.jupiter.api.Test
                        @org.junit.jupiter.api.DisplayName("f")
                        public void f() {
                        }
                        @org.junit.jupiter.api.Test
                        @org.junit.jupiter.api.DisplayName("g")
                        public void g() {
                        }
                    }"""
            )
        ));
    }

    @Test
    public void macrosInTestsGenerateImports() {
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

        var result = JavaCodeGenerator.compileNamespace(node, JavaTargetConfig.stub(), SubtypeLookup.EMPTY);

        assertThat(serialise(result), contains(
            equalTo(
                """
                    package example.project;
                    
                    import static org.hamcrest.MatcherAssert.assertThat;
                    import static org.hamcrest.Matchers.equalTo;

                    public class Project {
                        @org.junit.jupiter.api.Test
                        @org.junit.jupiter.api.DisplayName("x")
                        public void x() {
                            assertThat(1, equalTo(2));
                        }
                    }"""
            )
        ));
    }

    private List<String> serialise(List<JavaOrdinaryCompilationUnitNode> result) {
        return result
            .stream()
            .map(compilationUnit -> serialiseToString(
                    compilationUnit,
                    JavaSerialiser::serialiseOrdinaryCompilationUnit
                )
            )
            .collect(Collectors.toList());
    }
}
