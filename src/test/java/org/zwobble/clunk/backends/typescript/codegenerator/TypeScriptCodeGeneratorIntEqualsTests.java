package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorIntEqualsTests {
    @Test
    public void intEqualsIsCompiledToEqualsOperation() {
        var node = Typed.intEquals(
            Typed.localReference("a", Types.INT),
            Typed.localReference("b", Types.INT)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("a === b"));
    }
}
