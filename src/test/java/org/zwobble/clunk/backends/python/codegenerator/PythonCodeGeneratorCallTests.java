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
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorCallTests {
    @Test
    public void callToStaticFunctionsAreCompiledToCalls() {
        var node = new TypedCallNode(
            Typed.reference(
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
}
