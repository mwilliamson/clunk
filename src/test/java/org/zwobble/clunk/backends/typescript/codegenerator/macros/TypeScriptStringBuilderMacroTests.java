package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGenerator;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGeneratorContext;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.Matchers.isSequence;

public class TypeScriptStringBuilderMacroTests {
    @Test
    public void callToStringBuilderIsCompiledToEmptyArray() {
        var node = Typed.callConstructor(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER
        );
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[]"));
        assertThat(context.imports(), isSequence());
    }

    @Test
    public void stringBuilderAppendIsCompiledToArrayPush() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "builder",
                Types.STRING_BUILDER
            ))
            .methodName("append")
            .positionalArgs(List.of(Typed.string("hello")))
            .type(Types.INT)
            .build();

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.push(\"hello\")"));
    }

    @Test
    public void stringBuilderBuildIsCompiledToArrayJoin() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "builder",
                Types.STRING_BUILDER
            ))
            .methodName("build")
            .positionalArgs(List.of())
            .type(Types.UNIT)
            .build();

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.join(\"\")"));
    }
}
