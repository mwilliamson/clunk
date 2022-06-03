package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorFunctionTests {
    @Test
    public void functionIsCompiledToStaticMethod() {
        var node = TypedFunctionNode.builder()
            .name("f")
            .addParam(Typed.param("x", StringType.INSTANCE))
            .addParam(Typed.param("y", IntType.INSTANCE))
            .returnType(BoolType.INSTANCE)
            .addBodyStatement(Typed.returnStatement(Typed.boolFalse()))
            .build();

        var result = JavaCodeGenerator.compileFunction(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseClassBodyDeclaration);
        assertThat(string, equalTo(
            """
                public static boolean f(String x, int y) {
                    return false;
                }
                """
        ));
    }
}
