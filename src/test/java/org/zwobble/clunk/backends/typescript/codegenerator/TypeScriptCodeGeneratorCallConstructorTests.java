package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorCallConstructorTests {
    @Test
    public void callToRecordConstructorsAreCompiledToConstructorCalls() {
        var recordType = Types.recordType(NamespaceId.source("example"), "Id");
        var node = Typed.callConstructor(
            Typed.localReference("Id", Types.metaType(recordType)),
            List.of(Typed.intLiteral(123)),
            recordType
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Id(123)"));
    }

    @Test
    public void whenNonGenericTypeHasMacroThenConstructorCallIsCompiledUsingMacro() {
        var node = Typed.callConstructor(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER
        );
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[]"));
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
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Array<string>()"));
        assertThat(context.imports(), empty());
    }
}
