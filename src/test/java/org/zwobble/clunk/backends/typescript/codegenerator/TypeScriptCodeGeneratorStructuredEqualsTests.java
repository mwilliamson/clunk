package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorStructuredEqualsTests {
    @Test
    public void structuredEqualsIsCompiledToIsEqualMethodCall() {
        var node = Typed.structuredEquals(
            Typed.localReference("a", Types.OBJECT),
            Typed.localReference("b", Types.OBJECT)
        );

        var context = TypeScriptCodeGeneratorContext.stub();
        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("lodash_isEqual(a, b)"));
        assertThat(context.imports(), contains(
            TypeScript.import_("lodash", List.of(TypeScript.importNamedMember("isEqual", "lodash_isEqual")))
        ));
    }
}
