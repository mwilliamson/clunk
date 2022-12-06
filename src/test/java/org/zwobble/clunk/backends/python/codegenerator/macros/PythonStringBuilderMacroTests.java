package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallConstructorNode;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGeneratorContext;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonStringBuilderMacroTests {
    @Test
    public void callToStringBuilderIsCompiledToEmptyList() {
        var node = new TypedCallConstructorNode(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER,
            NullSource.INSTANCE
        );
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[]"));
        assertThat(context.imports(), empty());
    }

    @Test
    public void stringBuilderAppendIsCompiledToListAppend() {
        var node = new TypedCallMethodNode(
            Typed.localReference(
                "builder",
                Types.STRING_BUILDER
            ),
            "append",
            List.of(Typed.string("hello")),
            Types.INT,
            NullSource.INSTANCE
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.append(\"hello\")"));
    }

    @Test
    public void stringBuilderBuildIsCompiledToStringJoin() {
        var node = new TypedCallMethodNode(
            Typed.localReference(
                "builder",
                Types.STRING_BUILDER
            ),
            "build",
            List.of(),
            Types.UNIT,
            NullSource.INSTANCE
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("\"\".join(builder)"));
    }
}
