package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;
import org.zwobble.clunk.backends.python.ast.PythonFunctionNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserFunctionTests {
    @Test
    public void emptyFunction() {
        var node = PythonFunctionNode.builder().name("make_it_so").build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so():
                pass
            """));
    }

    @Test
    public void functionWithParams() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .addParam("x")
            .addParam("y")
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so(x, y):
                pass
            """));
    }

    @Test
    public void functionWithBody() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .addBodyStatement(Python.returnStatement(Python.FALSE))
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so():
                return False
            """));
    }
}
