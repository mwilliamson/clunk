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
    public void functionWithDecorators() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .addDecorator(Python.reference("x"))
            .addDecorator(Python.reference("y"))
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            @x
            @y
            def make_it_so():
                pass
            """));
    }

    @Test
    public void selfParam() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .withSelf()
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so(self):
                pass
            """));
    }

    @Test
    public void positionalParamsArePositionalOnly() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .addPositionalParam("x")
            .addPositionalParam("y")
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so(x, y, /):
                pass
            """));
    }

    @Test
    public void whenFunctionHasBothSelfAndPositionalParamsThenParamsArePositionalOnly() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .withSelf()
            .addPositionalParam("x")
            .addPositionalParam("y")
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so(self, x, y, /):
                pass
            """));
    }

    @Test
    public void keywordParamsAreKeywordOnly() {
        var node = PythonFunctionNode.builder()
            .name("make_it_so")
            .addKeywordParam("x")
            .addKeywordParam("y")
            .build();

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            def make_it_so(*, x, y):
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
