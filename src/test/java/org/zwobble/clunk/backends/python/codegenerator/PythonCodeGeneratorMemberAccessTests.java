package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Types;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorMemberAccessTests {
    @Test
    public void memberAccessIsCompiledToAttributeAccess() {
        var recordType = new RecordType(NamespaceName.fromParts("example"), "Id");
        var node = Typed.memberAccess(
            Typed.localReference("id", recordType),
            "value",
            Types.INT
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("id.value"));
    }

    @Test
    public void memberNameInLowerCamelCaseIsConvertedToSnakeCase() {
        var recordType = new RecordType(NamespaceName.fromParts("example"), "User");
        var node = Typed.memberAccess(
            Typed.localReference("user", recordType),
            "fullName",
            Types.STRING
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("user.full_name"));
    }

    @Test
    public void memberNameInUpperCamelCaseIsUnchanged() {
        var namespaceType = new NamespaceType(NamespaceName.fromParts("example"), Map.of());
        var node = Typed.memberAccess(
            Typed.localReference("example", namespaceType),
            "FullName",
            Types.recordType(NamespaceName.fromParts("example"), "FullName")
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("example.FullName"));
    }
}
