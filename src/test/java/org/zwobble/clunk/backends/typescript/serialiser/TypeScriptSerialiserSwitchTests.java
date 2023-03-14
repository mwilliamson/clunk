package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserSwitchTests {
    @Test
    public void canSerialiseSwitchWithCases() {
        var node = TypeScript.switchStatement(
            TypeScript.numberLiteral(42),
            List.of(
                TypeScript.switchCase(
                    TypeScript.numberLiteral(0),
                    List.of(
                        TypeScript.returnStatement(TypeScript.boolTrue())
                    )
                ),
                TypeScript.switchCase(
                    TypeScript.numberLiteral(1),
                    List.of(
                        TypeScript.returnStatement(TypeScript.boolFalse())
                    )
                )
            )
        );

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            switch (42) {
                case 0:
                    return true;
                case 1:
                    return false;
            }
            """));
    }
}
