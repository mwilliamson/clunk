package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorCallConstructorTests {
    @Test
    public void recordConstructorCallsAreCompiledToCalls() {
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var node = Typed.callConstructor(
            Typed.localReference(
                "Id",
                Types.metaType(recordType)
            ),
            List.of(Typed.intLiteral(123)),
            recordType
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("Id(123)"));
    }

    @Test
    public void whenTypeHasMacroThenConstructorCallIsCompiledUsingMacro() {
        var node = Typed.callConstructor(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER
        );
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[]"));
        assertThat(context.imports(), empty());
    }
}
