package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedReturnNode;
import org.zwobble.clunk.backends.python.ast.PythonStatementNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorReturnTests {
    @Test
    public void returnNodeGeneratesReturn() {
        var node = Typed.returnStatement(Typed.boolFalse());

        var result = compileFunctionStatement(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo("return False\n"));
    }

    private PythonStatementNode compileFunctionStatement(TypedReturnNode node) {
        return PythonCodeGenerator.DEFAULT.compileFunctionStatement(node);
    }
}
