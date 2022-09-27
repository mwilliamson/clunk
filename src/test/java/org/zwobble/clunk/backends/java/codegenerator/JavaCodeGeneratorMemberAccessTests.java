package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorMemberAccessTests {
    @Test
    public void memberAccessOnRecordIsCompiledToMethodCall() {
        var recordType = new RecordType(NamespaceName.fromParts("example"), "Id");
        var node = Typed.memberAccess(
            Typed.localReference("id", recordType),
            "value",
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("id.value()"));
    }

    @Test
    public void memberAccessOnNamespaceIsCompiledToMemberAccess() {
        var namespaceType = new NamespaceType(NamespaceName.fromParts("example"), Map.of());
        var node = Typed.memberAccess(
            Typed.localReference("example", namespaceType),
            "value",
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("example.value"));
    }
}
