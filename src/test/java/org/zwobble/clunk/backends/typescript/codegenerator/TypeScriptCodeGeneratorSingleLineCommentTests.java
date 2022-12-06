package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorSingleLineCommentTests {
    @Test
    public void singleLineCommentInFunctionIsCompiledToSingleLineComment() {
        var node = Typed.singleLineComment(" Beware.");

        var result = TypeScriptCodeGenerator.compileFunctionStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatements);
        assertThat(string, equalTo("// Beware.\n"));
    }

    @Test
    public void singleLineCommentInNamespaceIsCompiledToSingleLineComment() {
        var node = Typed.singleLineComment(" Beware.");

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo("// Beware.\n"));
    }
}
