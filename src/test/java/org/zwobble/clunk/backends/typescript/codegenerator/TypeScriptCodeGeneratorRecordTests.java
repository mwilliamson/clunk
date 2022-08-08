package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
            .addSupertype(Typed.typeLevelReference("Supertype", Types.sealedInterfaceType(NamespaceName.fromParts(), "Supertype")))
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
    public void propertiesAreCompiledToGetters() {
        var node = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Example")
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
}
