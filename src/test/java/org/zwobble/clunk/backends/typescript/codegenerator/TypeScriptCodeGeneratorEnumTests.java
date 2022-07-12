package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedEnumNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.EnumType;
import org.zwobble.clunk.types.NamespaceName;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorEnumTests {
    @Test
    public void enumIsCompiledToEnum() {
        var enumType = new EnumType(NamespaceName.fromParts("example", "project"), "NoteType", List.of("FOOTNOTE"));
        var node = new TypedEnumNode(enumType, NullSource.INSTANCE);
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                enum NoteType {
                    FOOTNOTE,
                }
                """
        ));
    }
}
