package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserTypeDeclarationTests {
    @Test
    public void canSerialiseUnionMembers() {
        var node = TypeScript.typeDeclaration("A", TypeScript.reference("B"));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("type A = B;\n"));
    }
}
