package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class JavaCodeGeneratorMemberDefinitionAccessTests {
    @Test
    public void memberDefinitionAccessIsCompiledToMethodReference() {
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var node = Typed.memberDefinitionReference(
            Typed.localReference("Id", Types.metaType(recordType)),
            "value",
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("example.Id::value"));
    }
}
