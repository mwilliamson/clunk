package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

public class SubtypeRelationsTests {
    private static final NamespaceName NAMESPACE_NAME = NamespaceName.fromParts("Example");
    private static final InterfaceType sealedInterfaceTypeOne = Types.interfaceType(NAMESPACE_NAME, "SealedInterface1");
    private static final InterfaceType sealedInterfaceTypeTwo = Types.interfaceType(NAMESPACE_NAME, "SealedInterface2");
    private static final RecordType recordTypeOne = Types.recordType(NAMESPACE_NAME, "RecordType1");
    private static final RecordType recordTypeTwo = Types.recordType(NAMESPACE_NAME, "RecordType2");

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
}
