package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserIfStatementTests {
    @Test
    public void singleConditionalBranch() {
        var node = Python.ifStatement(
            List.of(
                Python.conditionalBranch(Python.TRUE, List.of(
                    Python.returnStatement(Python.intLiteral(42))
                ))
            ),
            List.of()
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            if True:
                return 42
            """
        ));
    }

    @Test
    public void multipleConditionalBranches() {
        var node = Python.ifStatement(
            List.of(
                Python.conditionalBranch(Python.reference("a"), List.of(
                    Python.returnStatement(Python.intLiteral(42))
                )),

                Python.conditionalBranch(Python.reference("b"), List.of(
                    Python.returnStatement(Python.intLiteral(47))
                )),

                Python.conditionalBranch(Python.reference("c"), List.of(
                    Python.returnStatement(Python.intLiteral(52))
                ))
            ),
            List.of()
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            if a:
                return 42
            elif b:
                return 47
            elif c:
                return 52
            """
        ));
    }

    @Test
    public void elseBranch() {
        var node = Python.ifStatement(
            List.of(
                Python.conditionalBranch(Python.TRUE, List.of(
                    Python.returnStatement(Python.intLiteral(42))
                ))
            ),
            List.of(
                Python.returnStatement(Python.intLiteral(47))
            )
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);
        
        assertThat(result, equalTo(
            """
            if True:
                return 42
            else:
                return 47
            """
        ));
    }
}
