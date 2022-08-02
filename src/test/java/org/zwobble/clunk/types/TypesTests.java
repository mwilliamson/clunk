package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TypesTests {
    @Test
    public void typesAreSubTypesOfThemselves() {
        var subtypeRelations = SubtypeRelations.EMPTY;
        assertThat(subtypeRelations.isSubType(Types.BOOL, Types.BOOL), equalTo(true));
        assertThat(subtypeRelations.isSubType(Types.INT, Types.INT), equalTo(true));
        assertThat(subtypeRelations.isSubType(Types.OBJECT, Types.OBJECT), equalTo(true));
        assertThat(subtypeRelations.isSubType(Types.STRING, Types.STRING), equalTo(true));
    }

    @Test
    public void scalarTypesAreNotSubTypesOfEachOther() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(Types.BOOL, Types.INT);

        assertThat(result, equalTo(false));
    }

    @Test
    public void allTypesAreSubTypeOfObject() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(Types.BOOL, Types.OBJECT);

        assertThat(result, equalTo(true));
    }
}
