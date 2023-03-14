package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedEnumNode;
import org.zwobble.clunk.backends.python.ast.PythonImportNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.EnumType;
import org.zwobble.clunk.types.NamespaceId;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.isSequence;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorEnumTests {
    @Test
    public void enumIsCompiledToEnum() {
        var enumType = new EnumType(NamespaceId.source("example", "project"), "NoteType", List.of("FOOTNOTE"));
        var node = new TypedEnumNode(enumType, NullSource.INSTANCE);
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileNamespaceStatement(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                class NoteType(enum.Enum):
                    FOOTNOTE = enum.auto()
                """
        ));
        assertThat(context.imports(), isSequence(equalTo(new PythonImportNode(List.of("enum")))));
    }
}
