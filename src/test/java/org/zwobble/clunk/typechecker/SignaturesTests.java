package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;
import static org.zwobble.clunk.matchers.OptionalMatcher.present;

public class SignaturesTests {
    @Test
    public void methodHasSignature() {
        var typeParameter = TypeParameter.function(NamespaceName.fromParts(), "X", "f", "A");
        var methodType = new MethodType(
            Optional.of(List.of(typeParameter)),
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context);

        assertThat(result, cast(
            SignatureMethod.class,
            has("typeParams", present(contains(equalTo(typeParameter)))),
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

        assertThat(result, cast(
            SignatureStaticFunction.class,
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

        assertThat(result, cast(
            SignatureConstructorRecord.class,
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(recordType))
        ));
    }

    @Test
    public void stringBuilderIsConstructedWithoutAnyArguments() {
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(Types.metaType(Types.STRING_BUILDER), context);

        assertThat(result, cast(
            SignatureConstructorStringBuilder.class,
            has("positionalParams", empty()),
            has("returnType", equalTo(Types.STRING_BUILDER))
        ));
    }
}
