package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorCallMethodTests {
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.y(123)"));
    }

    @Test
    public void whenReceiverTypeHasMacroThenMethodCallIsCompiledUsingMacro() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "length",
            List.of(),
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.size()"));
    }
}
