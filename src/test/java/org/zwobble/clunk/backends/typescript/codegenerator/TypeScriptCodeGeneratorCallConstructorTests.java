package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallConstructorNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorCallConstructorTests {
    @Test
    public void callToRecordConstructorsAreCompiledToConstructorCalls() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Id");
        var node = new TypedCallConstructorNode(
            Typed.localReference("Id", Types.metaType(recordType)),
            List.of(Typed.intLiteral(123)),
            recordType,
            NullSource.INSTANCE
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Id(123)"));
    }

    @Test
    public void whenNonGenericTypeHasMacroThenConstructorCallIsCompiledUsingMacro() {
        var node = new TypedCallConstructorNode(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER,
            NullSource.INSTANCE
        );
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[]"));
        assertThat(context.imports(), empty());
    }

    @Test
    public void whenGenericTypeHasMacroThenConstructorCallIsCompiledUsingMacro() {
        var node = new TypedCallConstructorNode(
            Typed.localReference("MutableList", Types.metaType(Types.mutableList(Types.STRING))),
            List.of(),
            Types.mutableList(Types.STRING),
            NullSource.INSTANCE
        );
        var context = TypeScriptCodeGeneratorContext.stub();

        var result = TypeScriptCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        // TODO: should be new Array<string>
        assertThat(string, equalTo("new Array<any>()"));
        assertThat(context.imports(), empty());
    }
}
