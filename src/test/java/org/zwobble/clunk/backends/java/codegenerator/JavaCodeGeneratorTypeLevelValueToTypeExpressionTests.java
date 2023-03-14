package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorTypeLevelValueToTypeExpressionTests {
    @Test
    public void referencesToBuiltinsAreCompiledToAppropriateReference() {
        var value = Types.BOOL;
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.typeLevelValueToTypeExpression(value, false, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void interfaceTypesAreCompiledToFullyQualifiedReferences() {
        var value = Types.interfaceType(NamespaceId.source("example", "project"), "Interface");
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.typeLevelValueToTypeExpression(value, false, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("example.project.Interface"));
    }

    @Test
    public void recordTypesAreCompiledToFullyQualifiedReferences() {
        var value = Types.recordType(NamespaceId.source("example", "project"), "Record");
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.typeLevelValueToTypeExpression(value, false, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("example.project.Record"));
    }

    @Test
    public void constructedTypesAreCompiledByDecomposition() {
        var value = Types.construct(Types.LIST_CONSTRUCTOR, List.of(Types.BOOL));
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.typeLevelValueToTypeExpression(value, false, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("java.util.List<Boolean>"));
    }
}
