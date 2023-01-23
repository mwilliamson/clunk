package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGenerator;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGeneratorContext;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
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
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "builder",
                Types.STRING_BUILDER
            ))
            .methodName("append")
            .positionalArgs(List.of(Typed.string("hello")))
            .type(Types.INT)
            .build();

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.append(\"hello\")"));
    }

    @Test
    public void stringBuilderBuildIsCompiledToToString() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "builder",
                Types.STRING_BUILDER
            ))
            .methodName("build")
            .positionalArgs(List.of())
            .type(Types.UNIT)
            .build();

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.toString()"));
    }
}
