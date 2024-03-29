package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorTestTests {
    @Test
    public void testIsCompiledToMethod() {
        var node = TypedTestNode.builder()
            .name("can assign bool")
            .addBodyStatement(Typed.var("x", Typed.boolFalse()))
            .build();

        var result = JavaCodeGenerator.compileTest(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseClassBodyDeclaration);
        assertThat(string, equalTo(
            """
                @org.junit.jupiter.api.Test
                @org.junit.jupiter.api.DisplayName("can assign bool")
                public void canAssignBool() {
                    var x = false;
                }
                """
        ));
    }
}
