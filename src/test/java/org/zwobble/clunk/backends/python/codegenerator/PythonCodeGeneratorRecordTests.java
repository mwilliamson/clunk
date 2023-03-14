package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToDataClass() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @dataclasses.dataclass(frozen=True)
                class Example:
                    first: str
                    second: int
                """
        ));
    }

    @Test
    public void fieldNamesArePythonized() {
        var node = TypedRecordNode.builder("User")
            .addField(Typed.recordField("fullName", Typed.typeLevelString()))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @dataclasses.dataclass(frozen=True)
                class User:
                    full_name: str
                """
        ));
    }

    @Test
    public void whenRecordIsSubtypeOfSealedInterfaceThenAcceptMethodIsGenerated() {
        var node = TypedRecordNode.builder("ExampleRecord")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .addSupertype(Typed.typeLevelReference("Supertype", Types.sealedInterfaceType(NamespaceId.source(), "Supertype")))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @dataclasses.dataclass(frozen=True)
                class ExampleRecord:
                    first: str
                    second: int
                    def accept(self, visitor, /):
                        return visitor.visit_example_record(self)
                """
        ));
    }

    @Test
    public void whenRecordIsSubtypeOfUnsealedInterfaceThenNoAcceptMethodIsGenerated() {
        var node = TypedRecordNode.builder("ExampleRecord")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .addSupertype(Typed.typeLevelReference("Supertype", Types.unsealedInterfaceType(NamespaceId.source(), "Supertype")))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @dataclasses.dataclass(frozen=True)
                class ExampleRecord:
                    first: str
                    second: int
                """
        ));
    }

    @Test
    public void propertiesAreCompiledToProperties() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .addProperty(Typed.property(
                "value",
                Typed.typeLevelString(),
                List.of(Typed.returnStatement(Typed.string("hello")))
            ))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @dataclasses.dataclass(frozen=True)
                class Example:
                    @property
                    def value(self):
                        return "hello"
                """
        ));
    }

    @Test
    public void propertyNamesArePythonized() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "User")
            .addProperty(Typed.property(
                "fullName",
                Typed.typeLevelString(),
                List.of(Typed.returnStatement(Typed.string("Bob")))
            ))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @dataclasses.dataclass(frozen=True)
                class User:
                    @property
                    def full_name(self):
                        return "Bob"
                """
        ));
    }

    @Test
    public void functionsAreCompiledToMethods() {
        var node = TypedRecordNode.builder(NamespaceId.source("example", "project"), "Example")
            .addMethod(TypedFunctionNode.builder()
                // Use a name that tests Pythonization of identifiers
                .name("fullName")
                .returnType(Typed.typeLevelString())
                .addBodyStatement(Typed.returnStatement(Typed.string("Bob")))
                .build()
            )
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @dataclasses.dataclass(frozen=True)
                class Example:
                    def full_name(self):
                        return "Bob"
                """
        ));
    }
}
