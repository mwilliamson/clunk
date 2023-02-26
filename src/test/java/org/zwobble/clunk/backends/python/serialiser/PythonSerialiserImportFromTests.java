package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.PythonImportFromNode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserImportFromTests {
    @Test
    public void canSerialiseImportFromWithOneName() {
        var node = new PythonImportFromNode(List.of("datetime"), List.of("date"));

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("from datetime import date\n"));
    }

    @Test
    public void canSerialiseImportFromWithMultipleNames() {
        var node = new PythonImportFromNode(List.of("datetime"), List.of("date", "datetime", "timedelta"));

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("from datetime import date, datetime, timedelta\n"));
    }
}
