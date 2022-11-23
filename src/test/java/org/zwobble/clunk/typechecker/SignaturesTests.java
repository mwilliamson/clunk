package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;

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
        var methodType = Types.methodType(
            NamespaceName.fromParts("example"),
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context, NullSource.INSTANCE);

        assertThat(result, cast(
            SignatureNonGeneric.class,
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(Types.INT))
        ));
    }

    @Test
    public void genericCallableHasGenericSignature() {
        var namespaceName = NamespaceName.fromParts("example");
        var typeParameter = TypeParameter.function(namespaceName, "X", "f", "A");
        var methodType = Types.methodType(
            namespaceName,
            List.of(typeParameter),
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context, NullSource.INSTANCE);

        assertThat(result, cast(
            SignatureGeneric.class,
            has("typeParams", contains(equalTo(typeParameter)))
        ));
    }

    @Test
    public void whenRecordHasNoConstructorThenErrorIsThrown() {
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "A");
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceName.fromParts("other"));

        var result = assertThrows(
            NoConstructorError.class,
            () -> Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE)
        );

        assertThat(result.getMessage(), equalTo("example.A does not have a constructor"));
    }

    @Test
    public void whenRecordConstructorIsPublicThenConstructorCanBeCalledFromAnyNamespace() {
        var namespaceName = NamespaceName.fromParts("example");
        var recordType = Types.recordType(namespaceName, "A");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(namespaceName, List.of(), recordType, Visibility.PUBLIC))
            .enterNamespace(NamespaceName.fromParts("other"));

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, cast(SignatureNonGeneric.class));
    }

    @Test
    public void whenRecordConstructorIsPrivateThenConstructorCanBeCalledFromSameNamespace() {
        var namespaceName = NamespaceName.fromParts("example");
        var recordType = Types.recordType(namespaceName, "A");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(namespaceName, List.of(), recordType, Visibility.PRIVATE))
            .enterNamespace(namespaceName);

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, cast(SignatureNonGeneric.class));
    }

    @Test
    public void whenRecordConstructorIsPrivateThenConstructorCannotBeCalledFromOtherNamespace() {
        var namespaceName = NamespaceName.fromParts("example");
        var recordType = Types.recordType(namespaceName, "A");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(namespaceName, List.of(), recordType, Visibility.PRIVATE))
            .enterNamespace(NamespaceName.fromParts("other"));

        var result = assertThrows(
            NotVisibleError.class,
            () -> Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE)
        );

        assertThat(result.getMessage(), equalTo("constructor () -> example.A is not visible from other namespaces"));
    }

    @Test
    public void recordConstructorHasPositionalParamsMatchingFieldsAndReturnsSelf() {
        var namespaceName = NamespaceName.fromParts("example");
        var recordType = Types.recordType(namespaceName, "Id");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(namespaceName, List.of(Types.INT), recordType, Visibility.PUBLIC));

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, cast(
            SignatureNonGeneric.class,
            has("positionalParams", contains(equalTo(Types.INT))),
            has("returnType", equalTo(recordType))
        ));
    }

    @Test
    public void genericRecordConstructorHasPositionalParamsMatchingFieldsAndReturnsSelf() {
        var namespaceName = NamespaceName.fromParts("example");
        var typeParameter = TypeParameter.invariant(namespaceName, "Id", "T");
        var recordType = Types.recordType(namespaceName, "Id");
        var constructorType = Types.constructorType(namespaceName, List.of(typeParameter), recordType, Visibility.PUBLIC);
        var typeConstructor = new TypeConstructor(List.of(typeParameter), recordType);
        var context = TypeCheckerContext.stub()
            .addConstructorType(constructorType);

        var result = Signatures.toSignature(Types.typeConstructorType(typeConstructor), context, NullSource.INSTANCE);

        assertThat(result, cast(
            SignatureGeneric.class,
            has("type", equalTo(Types.constructorType(
                namespaceName,
                List.of(typeParameter),
                List.of(typeParameter),
                Types.construct(typeConstructor, List.of(typeParameter)),
                Visibility.PUBLIC
            )))
        ));
    }
}
