package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TypeScriptCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToTypeScriptInterface() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", StringType.INSTANCE))
            .addField(Typed.recordField("second", IntType.INSTANCE))
            .build();

        var result = TypeScriptCodeGenerator.compileRecord(node);
        var stringBuilder = new StringBuilder();
        TypeScriptSerialiser.serialiseInterfaceDeclaration(result, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
                interface Example {
                    first: string;
                    second: number;
                }"""
        ));
    }
}
