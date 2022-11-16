package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallConstructorNode;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorCallConstructorTests {
    @Test
    public void callToRecordConstructorsAreCompiledToConstructorCalls() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Id");
        var node = new TypedCallConstructorNode(
            Typed.localReference("Id", Types.metaType(recordType)),
            List.of(Typed.intLiteral(123)),
            recordType,
            NullSource.INSTANCE
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Id(123)"));
        assertThat(context.imports(), contains(equalTo(Java.importType("example.Id"))));
    }

    @Test
    public void callToStringBuilderIsCompiledToConstructorCall() {
        var node = new TypedCallConstructorNode(
            Typed.localReference("StringBuilder", Types.metaType(Types.STRING_BUILDER)),
            List.of(),
            Types.STRING_BUILDER,
            NullSource.INSTANCE
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileExpression(node, context);

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new StringBuilder()"));
        assertThat(context.imports(), empty());
    }
}
