package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGenerator;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGeneratorContext;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaStringBuilderMacroTests {
    @Test
    public void callToStringBuilderIsCompiledToConstructorCall() {
        var node = Typed.callConstructor(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new StringBuilder()"));
        assertThat(context.imports(), empty());
    }

    @Test
    public void stringBuilderAppendIsCompiledToAppend() {
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.append(\"hello\")"));
    }

    @Test
    public void stringBuilderBuildIsCompiledToToString() {
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.toString()"));
    }
}
