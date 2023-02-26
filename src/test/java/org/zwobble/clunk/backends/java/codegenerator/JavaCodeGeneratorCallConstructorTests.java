package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Types;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorCallConstructorTests {
    @Test
    public void callToRecordConstructorsAreCompiledToConstructorCalls() {
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var node = Typed.callConstructor(
            Typed.localReference("Id", Types.metaType(recordType)),
            List.of(Typed.intLiteral(123)),
            recordType
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Id(123)"));
        assertThat(context.imports(), contains(equalTo(Java.importType("example.Id"))));
    }

    @Test
    public void whenReceiverIsMemberAccessThenTypeIsImportedDirectly() {
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "Id");
        var node = Typed.callConstructor(
            Typed.memberAccess(
                Typed.localReference("ids", new NamespaceType(namespaceId, Map.of())),
                "Id",
                Types.metaType(recordType)
            ),
            List.of(Typed.intLiteral(123)),
            recordType
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Id(123)"));
        assertThat(context.imports(), contains(equalTo(Java.importType("example.Id"))));
    }

    @Test
    public void whenNonGenericTypeHasMacroThenConstructorCallIsCompiledUsingMacro() {
        var node = Typed.callConstructor(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new StringBuilder()"));
        assertThat(context.imports(), empty());
    }

    @Test
    public void whenGenericTypeHasMacroThenConstructorCallIsCompiledUsingMacro() {
        var node = Typed.callConstructor(
            Typed.localReference("MutableList", Types.metaType(Types.mutableList(Types.STRING))),
            List.of(Typed.typeLevelReference("String", Types.STRING)),
            List.of(),
            Types.mutableList(Types.STRING)
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new java.util.ArrayList<String>()"));
        assertThat(context.imports(), empty());
    }
}
