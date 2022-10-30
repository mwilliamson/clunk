package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

public class SubtypeRelationsTests {
    private static final NamespaceName NAMESPACE_NAME = NamespaceName.fromParts("Example");
    private static final StructuredType recordTypeOne = Types.recordType(NAMESPACE_NAME, "RecordType1");
    private static final StructuredType recordTypeTwo = Types.recordType(NAMESPACE_NAME, "RecordType2");
    private static final StructuredType recordTypeThree = Types.recordType(NAMESPACE_NAME, "RecordType3");

    @Test
    public void whenTypeHasNoSubtypesThenSubtypesOfReturnsEmptyList() {
        var relations = SubtypeRelations.EMPTY
            .add(recordTypeOne, recordTypeTwo);

        var result = relations.subtypesOf(recordTypeThree);

        assertThat(result, empty());
    }

    @Test
    public void whenTypeHasSubtypesThenSubtypesOfReturnsThoseSubtypes() {
        var relations = SubtypeRelations.EMPTY
            .add(recordTypeOne, recordTypeThree)
            .add(recordTypeTwo, recordTypeThree);

        var result = relations.subtypesOf(recordTypeThree);

        assertThat(result, containsInAnyOrder(recordTypeOne, recordTypeTwo));
    }
}
