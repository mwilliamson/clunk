package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptGetterNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserGetterTests {
    @Test
    public void canSerialiseGetterWithBody() {
        var node = TypeScriptGetterNode.builder()
            .name("f")
            .returnType(TypeScript.reference("string"))
            .addBodyStatement(TypeScript.returnStatement(TypeScript.string("hello")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseClassBodyDeclaration);

        assertThat(result, equalTo("""
            get f(): string {
                return "hello";
            }
            """
        ));
    }
}
