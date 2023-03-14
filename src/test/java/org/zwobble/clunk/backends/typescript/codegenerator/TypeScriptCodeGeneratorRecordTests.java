package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToTypeScriptClass() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .build();
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                class Example {
                    readonly first: string;
                    readonly second: number;
                    
                    constructor(first: string, second: number) {
                        this.first = first;
                        this.second = second;
                    }
                }
                """
        ));
    }

    @Test
    public void whenRecordIsSubtypeOfSealedInterfaceThenTypePropertyIsDiscriminator() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .addSupertype(Typed.typeLevelReference("Supertype", Types.sealedInterfaceType(NamespaceId.source(), "Supertype")))
            .build();
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                class Example {
                    readonly type: "Example" = "Example";
                    readonly first: string;
                    readonly second: number;
                    
                    constructor(first: string, second: number) {
                        this.first = first;
                        this.second = second;
                    }
                }
                """
        ));
    }

    @Test
    public void whenRecordIsSubtypeOfUnsealedInterfaceThenNoDiscriminatorIsGenerated() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .addSupertype(Typed.typeLevelReference("Supertype", Types.unsealedInterfaceType(NamespaceId.source(), "Supertype")))
            .build();
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                class Example {
                    readonly first: string;
                    readonly second: number;
                    
                    constructor(first: string, second: number) {
                        this.first = first;
                        this.second = second;
                    }
                }
                """
        ));
    }

    @Test
    public void propertiesAreCompiledToGetters() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .addProperty(Typed.property(
                "value",
                Typed.typeLevelString(),
                List.of(Typed.returnStatement(Typed.string("hello")))
            ))
            .build();
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                class Example {
                    get value(): string {
                        return "hello";
                    }
                }
                """
        ));
    }

    @Test
    public void functionsAreCompiledToMethods() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .addMethod(TypedFunctionNode.builder()
                .name("fullName")
                .returnType(Typed.typeLevelString())
                .addBodyStatement(Typed.returnStatement(Typed.string("Bob")))
                .build()
            )
            .build();
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                class Example {
                    fullName(): string {
                        return "Bob";
                    }
                }
                """
        ));
    }
}
