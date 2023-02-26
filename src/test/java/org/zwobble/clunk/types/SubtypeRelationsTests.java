package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

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

        assertThat(result, empty());
    }

    @Test
    public void whenSealedInterfaceHasCasesThenSealedInterfaceCasesReturnsThoseCases() {
        var relations = SubtypeRelations.EMPTY
            .addSealedInterfaceCase(sealedInterfaceTypeOne, recordTypeOne)
            .addSealedInterfaceCase(sealedInterfaceTypeOne, recordTypeTwo);

        var result = relations.sealedInterfaceCases(sealedInterfaceTypeOne);

        assertThat(result, containsInAnyOrder(recordTypeOne, recordTypeTwo));
    }

    @Test
    public void whenRecordExtendsNoTypesThenExtendedTypesReturnsEmptyList() {
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeOne, sealedInterfaceTypeOne);

        var result = relations.extendedTypes(recordTypeTwo);

        assertThat(result, empty());
    }

    @Test
    public void whenRecordExtendsTypesThenExtendedTypesReturnsThoseTypes() {
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeOne, sealedInterfaceTypeOne)
            .addExtendedType(recordTypeOne, sealedInterfaceTypeTwo);

        var result = relations.extendedTypes(recordTypeOne);

        assertThat(result, containsInAnyOrder(sealedInterfaceTypeOne, sealedInterfaceTypeTwo));
    }

    @Test
    public void whenExtendedTypesHaveNoTypeParametersThenCanFindExtendedTypesOfConstructedType() {
        var recordTypeGeneric = Types.recordType(NAMESPACE_ID, "Record");
        var recordTypeConstructor = new TypeConstructor(
            List.of(TypeParameter.invariant(NAMESPACE_ID, "Record", "T")),
            recordTypeGeneric
        );
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeGeneric, sealedInterfaceTypeOne);

        var result = relations.extendedTypes(Types.construct(recordTypeConstructor, List.of(Types.STRING)));

        assertThat(result, containsInAnyOrder(sealedInterfaceTypeOne));
    }

    @Test
    public void whenExtendedTypesHaveTypeParametersThenCanFindExtendedTypesOfConstructedType() {
        var recordTypeGeneric = Types.recordType(NAMESPACE_ID, "Record");
        var recordTypeParameter = TypeParameter.invariant(NAMESPACE_ID, "Record", "T");
        var recordTypeConstructor = new TypeConstructor(
            List.of(recordTypeParameter),
            recordTypeGeneric
        );
        var interfaceTypeGeneric = Types.interfaceType(NAMESPACE_ID, "Interface");
        var interfaceTypeConstructor = new TypeConstructor(
            List.of(TypeParameter.invariant(NAMESPACE_ID, "Interface", "U")),
            interfaceTypeGeneric
        );
        var relations = SubtypeRelations.EMPTY
            .addExtendedType(recordTypeGeneric, Types.construct(interfaceTypeConstructor, List.of(recordTypeParameter)));

        var result = relations.extendedTypes(Types.construct(recordTypeConstructor, List.of(Types.STRING)));

        assertThat(result, containsInAnyOrder(Types.construct(interfaceTypeConstructor, List.of(Types.STRING))));
    }
}
