package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorCallMethodTests {
    @Test
    public void callToMethodsWithExplicitReceiverAreCompiledToCallsWithExplicitReceiver() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "x",
                Types.recordType(NamespaceId.source("example"), "X")
            ))
            .methodName("y")
            .positionalArgs(List.of(Typed.intLiteral(123)))
            .type(Types.INT)
            .build();

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.y(123)"));
    }

    @Test
    public void callToMethodsWithImplicitReceiverAreCompiledToCallsWithExplicitThisReceiver() {
        var node = TypedCallMethodNode.builder()
            .methodName("y")
            .positionalArgs(List.of(Typed.intLiteral(123)))
            .type(Types.INT)
            .build();

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("this.y(123)"));
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
