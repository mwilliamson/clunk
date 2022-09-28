package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
        var value = Types.interfaceType(NamespaceName.fromParts("example", "project"), "Interface");
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.typeLevelValueToTypeExpression(value, false, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("example.project.Interface"));
    }

    @Test
    public void recordTypesAreCompiledToFullyQualifiedReferences() {
        var value = Types.recordType(NamespaceName.fromParts("example", "project"), "Record");
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.typeLevelValueToTypeExpression(value, false, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("example.project.Record"));
    }
}
