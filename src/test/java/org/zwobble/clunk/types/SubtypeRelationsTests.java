package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.containsExactly;
import static org.zwobble.precisely.Matchers.equalTo;

public class SubtypeRelationsTests {
    private static final NamespaceId NAMESPACE_ID = NamespaceId.source("Example");
    private static final InterfaceType sealedInterfaceTypeOne = Types.interfaceType(NAMESPACE_ID, "SealedInterface1");
    private static final InterfaceType sealedInterfaceTypeTwo = Types.interfaceType(NAMESPACE_ID, "SealedInterface2");
    private static final RecordType recordTypeOne = Types.recordType(NAMESPACE_ID, "RecordType1");
    private static final RecordType recordTypeTwo = Types.recordType(NAMESPACE_ID, "RecordType2");

    @Test
    public void whenSealedInterfaceHasNoCasesThenSealedInterfaceCasesReturnsEmptyList() {
        var relations = SubtypeRelations.EMPTY
            .addSealedInterfaceCase(sealedInterfaceTypeOne, recordTypeOne);

        var result = relations.sealedInterfaceCases(sealedInterfaceTypeTwo);

        assertThat(result, containsExactly());
    }

    @Test
    public void whenSealedInterfaceHasCasesThenSealedInterfaceCasesReturnsThoseCases() {
        var relations = SubtypeRelations.EMPTY
            .addSealedInterfaceCase(sealedInterfaceTypeOne, recordTypeOne)
            .addSealedInterfaceCase(sealedInterfaceTypeOne, recordTypeTwo);

        var result = relations.sealedInterfaceCases(sealedInterfaceTypeOne);

        assertThat(result, containsExactly(equalTo(recordTypeOne), equalTo(recordTypeTwo)));
    }

    @Test
    public void whenGenericSealedInterfaceHasCasesThenSealedInterfaceCasesReturnsThoseCases() {
        var genericInterfaceTypeConstructor = new TypeConstructor(
            List.of(TypeParam.covariant(NAMESPACE_ID, "Option", "T")),
            Types.sealedInterfaceType(NAMESPACE_ID, "Option")
        );
        var genericRecordTypeConstructorOne = new TypeConstructor(
            List.of(TypeParam.covariant(NAMESPACE_ID, "Some", "T")),
            Types.recordType(NAMESPACE_ID, "Some")
        );
        var genericRecordTypeTwo = Types.recordType(NAMESPACE_ID, "None");
        var relations = SubtypeRelations.EMPTY
            .addSealedInterfaceCase(
                genericInterfaceTypeConstructor.genericType(),
                Types.construct(genericRecordTypeConstructorOne, List.of(genericInterfaceTypeConstructor.param(0)))
            )
            .addSealedInterfaceCase(
                genericInterfaceTypeConstructor.genericType(),
                genericRecordTypeTwo
            );

        var result = relations.sealedInterfaceCases(
            Types.construct(genericInterfaceTypeConstructor, List.of(Types.STRING))
        );

        assertThat(result, containsExactly(
            equalTo(Types.construct(genericRecordTypeConstructorOne, List.of(Types.STRING))),
            equalTo(genericRecordTypeTwo)
        ));
    }

    @Test
    public void whenRecordExtendsNoTypesThenExtendedTypesReturnsEmptyList() {
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeOne, sealedInterfaceTypeOne);

        var result = relations.extendedTypes(recordTypeTwo);

        assertThat(result, containsExactly());
    }

    @Test
    public void whenRecordExtendsTypesThenExtendedTypesReturnsThoseTypes() {
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeOne, sealedInterfaceTypeOne)
            .addExtendedType(recordTypeOne, sealedInterfaceTypeTwo);

        var result = relations.extendedTypes(recordTypeOne);

        assertThat(result, containsExactly(equalTo(sealedInterfaceTypeOne), equalTo(sealedInterfaceTypeTwo)));
    }

    @Test
    public void whenExtendedTypesHaveNoTypeParametersThenCanFindExtendedTypesOfConstructedType() {
        var recordTypeGeneric = Types.recordType(NAMESPACE_ID, "Record");
        var recordTypeConstructor = new TypeConstructor(
            List.of(TypeParam.invariant(NAMESPACE_ID, "Record", "T")),
            recordTypeGeneric
        );
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeGeneric, sealedInterfaceTypeOne);

        var result = relations.extendedTypes(Types.construct(recordTypeConstructor, List.of(Types.STRING)));

        assertThat(result, containsExactly(equalTo(sealedInterfaceTypeOne)));
    }

    @Test
    public void whenExtendedTypesHaveTypeParametersThenCanFindExtendedTypesOfConstructedType() {
        var recordTypeGeneric = Types.recordType(NAMESPACE_ID, "Record");
        var recordTypeParameter = TypeParam.invariant(NAMESPACE_ID, "Record", "T");
        var recordTypeConstructor = new TypeConstructor(
            List.of(recordTypeParameter),
            recordTypeGeneric
        );
        var interfaceTypeGeneric = Types.interfaceType(NAMESPACE_ID, "Interface");
        var interfaceTypeConstructor = new TypeConstructor(
            List.of(TypeParam.invariant(NAMESPACE_ID, "Interface", "U")),
            interfaceTypeGeneric
        );
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeGeneric, Types.construct(interfaceTypeConstructor, List.of(recordTypeParameter)));

        var result = relations.extendedTypes(Types.construct(recordTypeConstructor, List.of(Types.STRING)));

        assertThat(result, containsExactly(equalTo(Types.construct(interfaceTypeConstructor, List.of(Types.STRING)))));
    }
}
