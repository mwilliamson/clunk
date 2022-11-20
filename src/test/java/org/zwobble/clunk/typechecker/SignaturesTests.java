package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.clunk.matchers.CastMatcher.cast;
import static org.zwobble.clunk.matchers.HasMethodWithValue.has;

public class SignaturesTests {
    @Test
    public void whenTypeIsNotCallableThenErrorIsThrown() {
        var context = TypeCheckerContext.stub();

        var result = assertThrows(
            UnexpectedTypeError.class,
            () -> Signatures.toSignature(Types.INT, context, NullSource.INSTANCE)
        );

        assertThat(result.getExpected(), equalTo(Types.CALLABLE));
        assertThat(result.getActual(), equalTo(Types.INT));
    }

    @Test
    public void nonGenericCallableHasNonGenericSignature() {
        var methodType = new MethodType(
            NamespaceName.fromParts("example"),
            Optional.empty(),
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context, NullSource.INSTANCE);

        assertThat(result, cast(
            SignatureNonGenericCallable.class,
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(Types.INT))
        ));
    }

    @Test
    public void genericCallableHasGenericSignature() {
        var namespaceName = NamespaceName.fromParts("example");
        var typeParameter = TypeParameter.function(namespaceName, "X", "f", "A");
        var methodType = new MethodType(
            namespaceName,
            Optional.of(List.of(typeParameter)),
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context, NullSource.INSTANCE);

        assertThat(result, cast(
            SignatureGenericCallable.class,
            has("typeParams", contains(equalTo(typeParameter)))
        ));
    }

    @Test
    public void whenRecordConstructorIsPublicThenConstructorCanBeCalledFromAnyNamespace() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "A", Visibility.PUBLIC);
        var context = TypeCheckerContext.stub()
            .addConstructorType(recordType, List.of())
            .enterNamespace(NamespaceName.fromParts("other"));

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, cast(SignatureConstructorRecord.class));
    }

    @Test
    public void whenRecordConstructorIsPrivateThenConstructorCanBeCalledFromSameNamespace() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "A", Visibility.PRIVATE);
        var context = TypeCheckerContext.stub()
            .addConstructorType(recordType, List.of())
            .enterNamespace(NamespaceName.fromParts("example"));

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, cast(SignatureConstructorRecord.class));
    }

    @Test
    public void whenRecordConstructorIsPrivateThenConstructorCannotBeCalledFromOtherNamespace() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "A", Visibility.PRIVATE);
        var context = TypeCheckerContext.stub()
            .addConstructorType(recordType, List.of())
            .enterNamespace(NamespaceName.fromParts("other"));

        var result = assertThrows(
            NotVisibleError.class,
            () -> Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE)
        );

        assertThat(result.getMessage(), equalTo("The constructor for example.A is not visible from other namespaces"));
    }

    @Test
    public void recordConstructorHasPositionalParamsMatchingFieldsAndReturnsSelf() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Id");
        var context = TypeCheckerContext.stub()
            .addConstructorType(recordType, List.of(Types.INT));

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, cast(
            SignatureConstructorRecord.class,
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(recordType))
        ));
    }
}
