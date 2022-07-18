package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedMemberAccessNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorMemberAccessTests {
    @Test
    public void memberAccessIsCompiledToAttributeAccess() {
        var recordType = new RecordType(NamespaceName.fromParts("example"), "Id");
        var node = new TypedMemberAccessNode(
            Typed.reference("id", recordType),
            "value",
            Types.INT,
            NullSource.INSTANCE
        );

        var result = PythonCodeGenerator.DEFAULT.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("(id).value"));
    }
}
