package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorSingleLineCommentTests {
    @Test
    public void singleLineCommentInFunctionIsCompiledToSingleLineComment() {
        var node = Typed.singleLineComment(" Beware.");

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo("# Beware.\n"));
    }

    @Test
    public void singleLineCommentInNamespaceIsCompiledToSingleLineComment() {
        var node = Typed.singleLineComment(" Beware.");

        var result = PythonCodeGenerator.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo("# Beware.\n"));
    }
}
