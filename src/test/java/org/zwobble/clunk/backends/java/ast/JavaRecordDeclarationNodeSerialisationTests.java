package org.zwobble.clunk.backends.java.ast;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.java.ast.JavaSerialiser.serialiseRecordDeclaration;

public class JavaRecordDeclarationNodeSerialisationTests {
    @Test
    public void emptyRecord() {
        var node = new JavaRecordDeclarationNode(
            "Example"
        );

        var stringBuilder = new StringBuilder();
        serialiseRecordDeclaration(node, stringBuilder);

        assertThat(stringBuilder.toString(), equalTo(
            """
            public record Example() {
            }"""
        ));
    }
}
