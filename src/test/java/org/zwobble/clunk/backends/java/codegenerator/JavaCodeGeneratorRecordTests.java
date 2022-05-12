package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToJavaRecord() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", StringType.INSTANCE))
            .addField(Typed.recordField("second", IntType.INSTANCE))
            .build();

        var result = JavaCodeGenerator.compileRecord(NamespaceName.fromParts("example", "project"), node);

        var string = serialiseToString(result, JavaSerialiser::serialiseOrdinaryCompilationUnit);
        assertThat(string, equalTo(
            """
                package example.project;
                
                public record Example(String first, int second) {
                }"""
        ));
    }
}
