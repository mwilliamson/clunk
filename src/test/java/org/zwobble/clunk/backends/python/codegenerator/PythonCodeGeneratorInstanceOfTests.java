package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorInstanceOfTests {
    @Test
    public void instanceOfIsCompiledToIsinstanceCall() {
        var interfaceType = Types.sealedInterfaceType(NamespaceName.fromParts("example"), "Node");
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Add");
        var node = Typed.instanceOf(
            Typed.localReference("node", interfaceType),
            Typed.typeLevelReference("Add", Types.metaType(recordType))
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("isinstance(node, Add)"));
    }
}
