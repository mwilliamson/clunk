package org.zwobble.clunk.typechecker;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zwobble.precisely.Matchers.*;

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
            NamespaceId.source("example"),
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context, NullSource.INSTANCE);

        assertThat(result, instanceOf(
            SignatureNonGeneric.class,
            has("positionalParams", x -> x.positionalParams(), isSequence(equalTo(Types.INT))),
            has("returnType", x -> x.returnType(), equalTo(Types.INT))
        ));
    }

    @Test
    public void genericCallableHasGenericSignature() {
        var namespaceId = NamespaceId.source("example");
        var typeParameter = TypeParameter.method(namespaceId, "X", "f", "A");
        var methodType = Types.methodType(
            namespaceId,
            List.of(typeParameter),
            List.of(Types.INT),
            Types.INT
        );
        var context = TypeCheckerContext.stub();

        var result = Signatures.toSignature(methodType, context, NullSource.INSTANCE);

        assertThat(result, instanceOf(
            SignatureGeneric.class,
            has("typeParams", x -> x.typeParams(), isSequence(equalTo(typeParameter)))
        ));
    }

    @Test
    public void whenRecordHasNoConstructorThenErrorIsThrown() {
        var recordType = Types.recordType(NamespaceId.source("example"), "A");
        var context = TypeCheckerContext.stub()
            .enterNamespace(NamespaceId.source("other"));

        var result = assertThrows(
            NoConstructorError.class,
            () -> Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE)
        );

        assertThat(result.getMessage(), equalTo("example.A does not have a constructor"));
    }

    @Test
    public void whenRecordConstructorIsPublicThenConstructorCanBeCalledFromAnyNamespace() {
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "A");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(List.of(), recordType, Visibility.PUBLIC))
            .enterNamespace(NamespaceId.source("other"));

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, instanceOf(SignatureNonGeneric.class));
    }

    @Test
    public void whenRecordConstructorIsPrivateThenConstructorCanBeCalledFromSameNamespace() {
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "A");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(List.of(), recordType, Visibility.PRIVATE))
            .enterNamespace(namespaceId);

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, instanceOf(SignatureNonGeneric.class));
    }

    @Test
    public void whenRecordConstructorIsPrivateThenConstructorCannotBeCalledFromOtherNamespace() {
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "A");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(List.of(), recordType, Visibility.PRIVATE))
            .enterNamespace(NamespaceId.source("other"));

        var result = assertThrows(
            NotVisibleError.class,
            () -> Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE)
        );

        assertThat(result.getMessage(), equalTo("constructor () -> example.A is not visible from other namespaces"));
    }

    @Test
    public void recordConstructorHasPositionalParamsMatchingFieldsAndReturnsSelf() {
        var namespaceId = NamespaceId.source("example");
        var recordType = Types.recordType(namespaceId, "Id");
        var context = TypeCheckerContext.stub()
            .addConstructorType(Types.constructorType(List.of(Types.INT), recordType, Visibility.PUBLIC));

        var result = Signatures.toSignature(Types.metaType(recordType), context, NullSource.INSTANCE);

        assertThat(result, instanceOf(
            SignatureNonGeneric.class,
            has("positionalParams", x -> x.positionalParams(), isSequence(equalTo(Types.INT))),
            has("returnType", x -> x.returnType(), equalTo(recordType))
        ));
    }

    @Test
    public void genericRecordConstructorHasPositionalParamsMatchingFieldsAndReturnsSelf() {
        var namespaceId = NamespaceId.source("example");
        var typeParameter = TypeParameter.invariant(namespaceId, "Id", "T");
        var recordType = Types.recordType(namespaceId, "Id");
        var constructorType = Types.constructorType(List.of(typeParameter), recordType, Visibility.PUBLIC);
        var typeConstructor = new TypeConstructor(List.of(typeParameter), recordType);
        var context = TypeCheckerContext.stub()
            .addConstructorType(constructorType);

        var result = Signatures.toSignature(Types.typeConstructorType(typeConstructor), context, NullSource.INSTANCE);

        assertThat(result, instanceOf(
            SignatureGeneric.class,
            has("type", x -> x.type(), equalTo(Types.constructorType(
                List.of(typeParameter),
                List.of(typeParameter),
                Types.construct(typeConstructor, List.of(typeParameter)),
                Visibility.PUBLIC
            )))
        ));
    }
}
