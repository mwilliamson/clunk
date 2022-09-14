package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.types.MethodType;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class SignaturesTests {
    @Test
    public void methodHasSignature() {
        var methodType = new MethodType(
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context);

        assertThat(result, allOf(
            has("signatureType", equalTo(SignatureType.METHOD)),
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(Types.INT))
        ));
    }

    @Test
    public void staticFunctionHasSignature() {
        var functionType = new StaticFunctionType(
            NamespaceName.fromParts("Stdlib", "Math"),
            "abs",
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(functionType, context);

        assertThat(result, allOf(
            has("signatureType", equalTo(SignatureType.STATIC_METHOD)),
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(Types.INT))
        ));
    }

    @Test
    public void recordConstructorHasPositionalParamsMatchingFieldsAndReturnsSelf() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Id");
        var context = TypeCheckerContext.stub()
            .addFields(recordType, List.of(Typed.recordField("value", Typed.typeLevelInt())));

        var result = Signatures.toSignature(Types.metaType(recordType), context);

        assertThat(result, allOf(
            has("signatureType", equalTo(SignatureType.CONSTRUCTOR)),
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(recordType))
        ));
    }

    @Test
    public void stringBuilderIsConstructedWithoutAnyArguments() {
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(Types.metaType(Types.STRING_BUILDER), context);

        assertThat(result, allOf(
            has("signatureType", equalTo(SignatureType.CONSTRUCTOR)),
            has("positionalParams", empty()),
            has("returnType", equalTo(Types.STRING_BUILDER))
        ));
    }
}
