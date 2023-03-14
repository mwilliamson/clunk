package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.TypedEnumNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.EnumType;
import org.zwobble.clunk.types.NamespaceId;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorEnumTests {
    @Test
    public void enumIsCompiledToEnum() {
        var enumType = new EnumType(NamespaceId.source("example", "project"), "NoteType", List.of("FOOTNOTE"));
        var node = new TypedEnumNode(enumType, NullSource.INSTANCE);
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileEnum(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public enum NoteType {
                    FOOTNOTE
                }"""
        ));
    }
}
