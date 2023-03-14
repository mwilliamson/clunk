package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorInstanceOfTests {
    @Test
    public void instanceOfIsCompiledToEqualityOnTypeField() {
        var interfaceType = Types.sealedInterfaceType(NamespaceId.source("example"), "Node");
        var recordType = Types.recordType(NamespaceId.source("example"), "Add");
        var node = Typed.instanceOf(
            Typed.localReference("node", interfaceType),
            Typed.typeLevelReference("Add", recordType)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("node.type === \"Add\""));
    }
}
