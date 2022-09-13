package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorCallTests {
    @Test
    public void callToStaticFunctionsAreCompiledToCalls() {
        var node = new TypedCallNode(
            Typed.localReference(
                "abs",
                new StaticFunctionType(
                    NamespaceName.fromParts("Math"),
                    "abs",
                    List.of(Types.INT),
                    Types.INT
                )
            ),
            List.of(Typed.intLiteral(123)),
            Types.INT,
            NullSource.INSTANCE
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("abs(123)"));
    }

    @Test
    public void stringBuilderAppendIsCompiledToListAppend() {
        var node = new TypedCallNode(
            Typed.memberAccess(
                Typed.localReference(
                    "builder",
                    Types.STRING_BUILDER
                ),
                "append",
                Types.methodType(List.of(Types.STRING), Types.UNIT)
            ),
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
        var node = new TypedCallNode(
            Typed.memberAccess(
                Typed.localReference(
                    "builder",
                    Types.STRING_BUILDER
                ),
                "build",
                Types.methodType(List.of(), Types.STRING)
            ),
            List.of(),
            Types.UNIT,
            NullSource.INSTANCE
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("\"\".join(builder)"));
    }

    @Test
    public void callToStringBuilderIsCompiledToEmptyList() {
        var node = new TypedCallNode(
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
}
