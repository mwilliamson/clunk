package org.zwobble.clunk.backends.java;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.RecordFieldNode;
import org.zwobble.clunk.ast.RecordNode;
import org.zwobble.clunk.ast.StaticReferenceNode;
import org.zwobble.clunk.backends.java.ast.JavaSerialiser;
import org.zwobble.clunk.sources.NullSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JavaCodeGeneratorTests {
    @Test
    public void recordIsCompiledToJavaRecord() {
        var node = RecordNode.builder("Example")
            .addField(new RecordFieldNode("first", new StaticReferenceNode("String", NullSource.INSTANCE), NullSource.INSTANCE))
            .addField(new RecordFieldNode("second", new StaticReferenceNode("Int", NullSource.INSTANCE), NullSource.INSTANCE))
            .build();

        var result = JavaCodeGenerator.compileRecord(node);
        var stringBuilder = new StringBuilder();
        JavaSerialiser.serialiseRecordDeclaration(result, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
                public record Example(String first, int second) {
                }"""
        ));
    }
}
