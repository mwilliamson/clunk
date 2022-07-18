package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptClassDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
}
