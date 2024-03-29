package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptClassDeclarationNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptFunctionDeclarationNode;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserClassDeclarationTests {
    @Test
    public void emptyClass() {
        var node = TypeScriptClassDeclarationNode.builder("Example").build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            class Example {
            }
            """
        ));
    }

    @Test
    public void classWithOneField() {
        var node = TypeScriptClassDeclarationNode.builder("Example")
            .addField(TypeScript.classField("first", TypeScript.reference("string")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            class Example {
                readonly first: string;
                
                constructor(first: string) {
                    this.first = first;
                }
            }
            """
        ));
    }

    @Test
    public void classWithMultipleFields() {
        var node = TypeScriptClassDeclarationNode.builder("Example")
            .addField(TypeScript.classField("first", TypeScript.reference("string")))
            .addField(TypeScript.classField("second", TypeScript.reference("number")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
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
    public void constantFieldsAreInitialisedInline() {
        var node = TypeScriptClassDeclarationNode.builder("Example")
            .addField(TypeScript.classField("type", TypeScript.string("active"), TypeScript.string("active")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            class Example {
                readonly type: "active" = "active";
            }
            """
        ));
    }

    @Test
    public void constantFieldsAreNotIncludedInConstructor() {
        var node = TypeScriptClassDeclarationNode.builder("Example")
            .addField(TypeScript.classField("type", TypeScript.string("active"), TypeScript.string("active")))
            .addField(TypeScript.classField("first", TypeScript.reference("string")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            class Example {
                readonly type: "active" = "active";
                readonly first: string;
                
                constructor(first: string) {
                    this.first = first;
                }
            }
            """
        ));
    }

    @Test
    public void classWithMethod() {
        var node = TypeScriptClassDeclarationNode.builder("Example")
            .addMethod(
                TypeScriptFunctionDeclarationNode.builder()
                    .name("f")
                    .addParam(TypeScript.param("x", TypeScript.reference("number")))
                    .returnType(TypeScript.reference("string"))
                    .addBodyStatement(TypeScript.returnStatement(TypeScript.string("hello")))
                    .build()
            )
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            class Example {
                f(x: number): string {
                    return "hello";
                }
            }
            """
        ));
    }
}
