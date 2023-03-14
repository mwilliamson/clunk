package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserDictTests {
    @Test
    public void canSerialiseEmptyDict() {
        var node = Python.dict(List.of());

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("{}"));
    }

    @Test
    public void canSerialiseSingletonDict() {
        var node = Python.dict(List.of(
            Python.dictItem(Python.intLiteral(1), Python.intLiteral(2))
        ));

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("{1: 2}"));
    }

    @Test
    public void canSerialiseDictWithMultipleElements() {
        var node = Python.dict(List.of(
            Python.dictItem(Python.intLiteral(1), Python.intLiteral(2)),
            Python.dictItem(Python.intLiteral(3), Python.intLiteral(4)),
            Python.dictItem(Python.intLiteral(5), Python.intLiteral(6))
        ));

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("{1: 2, 3: 4, 5: 6}"));
    }
}
