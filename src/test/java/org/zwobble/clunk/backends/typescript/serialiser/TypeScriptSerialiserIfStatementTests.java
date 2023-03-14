package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserIfStatementTests {
    @Test
    public void singleConditionalBranch() {
        var node = TypeScript.ifStatement(
            List.of(
                TypeScript.conditionalBranch(TypeScript.boolTrue(), List.of(
                    TypeScript.returnStatement(TypeScript.numberLiteral(42))
                ))
            ),
            List.of()
        );

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("if (true) {\n    return 42;\n}\n"));
    }

    @Test
    public void multipleConditionalBranches() {
        var node = TypeScript.ifStatement(
            List.of(
                TypeScript.conditionalBranch(TypeScript.reference("a"), List.of(
                    TypeScript.returnStatement(TypeScript.numberLiteral(42))
                )),

                TypeScript.conditionalBranch(TypeScript.reference("b"), List.of(
                    TypeScript.returnStatement(TypeScript.numberLiteral(47))
                )),

                TypeScript.conditionalBranch(TypeScript.reference("c"), List.of(
                    TypeScript.returnStatement(TypeScript.numberLiteral(52))
                ))
            ),
            List.of()
        );

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            if (a) {
                return 42;
            } else if (b) {
                return 47;
            } else if (c) {
                return 52;
            }
            """
        ));
    }

    @Test
    public void elseBranch() {
        var node = TypeScript.ifStatement(
            List.of(
                TypeScript.conditionalBranch(TypeScript.boolTrue(), List.of(
                    TypeScript.returnStatement(TypeScript.numberLiteral(42))
                ))
            ),
            List.of(
                TypeScript.returnStatement(TypeScript.numberLiteral(47))
            )
        );

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);
        assertThat(result, equalTo(
            """
            if (true) {
                return 42;
            } else {
                return 47;
            }
            """
        ));
    }
}
