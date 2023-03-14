package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserForOfTests {
    @Test
    public void canSerialiseForOf() {
        var node = TypeScript.forOf(
            "x",
            TypeScript.reference("xs"),
            List.of(
                TypeScript.expressionStatement(TypeScript.call(
                    TypeScript.reference("print"),
                    List.of(TypeScript.reference("x"))
                ))
            )
        );

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            for (let x of xs) {
                print(x);
            }
            """));
    }
}
