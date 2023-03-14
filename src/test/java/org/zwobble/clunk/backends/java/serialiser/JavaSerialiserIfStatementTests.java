package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserIfStatementTests {
    @Test
    public void singleConditionalBranch() {
        var node = Java.ifStatement(
            List.of(
                Java.conditionalBranch(Java.boolTrue(), List.of(
                    Java.returnStatement(Java.intLiteral(42))
                ))
            ),
            List.of()
        );

        var result = serialiseToString(node, JavaSerialiser::serialiseStatement);

        assertThat(result, equalTo("if (true) {\n    return 42;\n}\n"));
    }

    @Test
    public void multipleConditionalBranches() {
        var node = Java.ifStatement(
            List.of(
                Java.conditionalBranch(Java.reference("a"), List.of(
                    Java.returnStatement(Java.intLiteral(42))
                )),

                Java.conditionalBranch(Java.reference("b"), List.of(
                    Java.returnStatement(Java.intLiteral(47))
                )),

                Java.conditionalBranch(Java.reference("c"), List.of(
                    Java.returnStatement(Java.intLiteral(52))
                ))
            ),
            List.of()
        );

        var result = serialiseToString(node, JavaSerialiser::serialiseStatement);

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
        var node = Java.ifStatement(
            List.of(
                Java.conditionalBranch(Java.boolTrue(), List.of(
                    Java.returnStatement(Java.intLiteral(42))
                ))
            ),
            List.of(
                Java.returnStatement(Java.intLiteral(47))
            )
        );

        var result = serialiseToString(node, JavaSerialiser::serialiseStatement);
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
