package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.precisely.Matchers.isSequence;

public class TypeScriptCodeGeneratorStructuredNotEqualTests {
    @Test
    public void structuredNotEqualIsCompiledToNegatedIsEqualMethodCall() {
        var node = Typed.structuredNotEqual(
            Typed.localReference("a", Types.OBJECT),
            Typed.localReference("b", Types.OBJECT)
        );

        var context = TypeScriptCodeGeneratorContext.stub();
        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("!lodash_isEqual(a, b)"));
        assertThat(context.imports(), isSequence(
            equalTo(TypeScript.import_("lodash", List.of(TypeScript.importNamedMember("isEqual", "lodash_isEqual"))))
        ));
    }
}
