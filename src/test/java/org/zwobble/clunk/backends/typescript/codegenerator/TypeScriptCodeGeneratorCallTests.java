package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallStaticFunctionNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorCallTests {
    @Test
    public void callToStaticFunctionsAreCompiledToCalls() {
        var node = new TypedCallStaticFunctionNode(
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

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("abs(123)"));
    }
}
