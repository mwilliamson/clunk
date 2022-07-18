package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorFunctionTests {
    @Test
    public void functionIsCompiledToStaticMethod() {
        var node = TypedFunctionNode.builder()
            .name("f")
            .addParam(Typed.param("x", Typed.typeLevelString()))
            .addParam(Typed.param("y", Typed.typeLevelInt()))
            .returnType(Typed.typeLevelBool())
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
