package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
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
    public void functionWithArgs() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .addArg("x")
            .addArg("y")
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so(x, y):
                pass
            """));
    }
}
