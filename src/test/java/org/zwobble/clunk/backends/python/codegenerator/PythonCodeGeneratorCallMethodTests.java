package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorCallMethodTests {
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

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.y(123)"));
    }

    @Test
    public void methodNamesArePythonized() {
        var node = new TypedCallMethodNode(
            Typed.localReference(
                "x",
                Types.recordType(NamespaceName.fromParts("example"), "X")
            ),
            "isValid",
            List.of(),
            Types.BOOL,
            NullSource.INSTANCE
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.is_valid()"));
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
