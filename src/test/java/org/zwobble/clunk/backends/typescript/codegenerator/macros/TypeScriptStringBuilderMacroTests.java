package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallConstructorNode;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGenerator;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGeneratorContext;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptStringBuilderMacroTests {
    @Test
    public void callToStringBuilderIsCompiledToEmptyArray() {
        var node = new TypedCallConstructorNode(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER,
            NullSource.INSTANCE
        );
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[]"));
        assertThat(context.imports(), empty());
    }

    @Test
    public void stringBuilderAppendIsCompiledToArrayPush() {
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

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.push(\"hello\")"));
    }

    @Test
    public void stringBuilderBuildIsCompiledToArrayJoin() {
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

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("builder.join(\"\")"));
    }
}
