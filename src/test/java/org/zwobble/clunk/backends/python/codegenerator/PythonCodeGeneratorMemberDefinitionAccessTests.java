package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class PythonCodeGeneratorMemberDefinitionAccessTests {
    @Test
    public void memberDefinitionAccessIsCompiledToString() {
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var node = Typed.memberDefinitionReference(
            Typed.localReference("Id", Types.metaType(recordType)),
            "value",
            Types.INT
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("\"value\""));
    }
}
