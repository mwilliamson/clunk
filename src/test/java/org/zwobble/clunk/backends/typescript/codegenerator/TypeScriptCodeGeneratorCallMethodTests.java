package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorCallMethodTests {
    @Test
    public void callToMethodsAreCompiledToCalls() {
        var node = new TypedCallMethodNode(
            Typed.localReference(
                "x",
                Types.recordType(NamespaceName.fromParts("example"), "X")
            ),
            "y",
            List.of(Typed.intLiteral(123)),
            Types.INT,
            NullSource.INSTANCE
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.y(123)"));
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
