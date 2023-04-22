package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptCodeGeneratorMemberDefinitionAccessTests {
    @Test
    public void memberDefinitionAccessIsCompiledToString() {
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var node = Typed.memberDefinitionReference(
            Typed.localReference("Id", Types.metaType(recordType)),
            "value",
            Types.INT
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("\"value\""));
    }
}
