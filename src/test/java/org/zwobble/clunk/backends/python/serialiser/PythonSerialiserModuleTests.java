package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;
import org.zwobble.clunk.backends.python.ast.PythonModuleNode;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserModuleTests {
    @Test
    public void moduleContainsStatements() {
        var node = new PythonModuleNode(List.of("example", "project"), List.of(
            Python.variableType("first", Python.reference("str")),
            Python.variableType("second", Python.reference("int"))
        ));

        var result = serialiseToString(node, PythonSerialiser::serialiseModule);

        assertThat(result, equalTo("""
            first: str
            second: int
            """));
    }
}
