package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TypesTests {
    @Test
    public void typesAreSubTypesOfThemselves() {
        assertThat(Types.isSubType(Types.BOOL, Types.BOOL), equalTo(true));
        assertThat(Types.isSubType(Types.INT, Types.INT), equalTo(true));
        assertThat(Types.isSubType(Types.OBJECT, Types.OBJECT), equalTo(true));
        assertThat(Types.isSubType(Types.STRING, Types.STRING), equalTo(true));
    }

    @Test
    public void scalarTypesAreNotSubTypesOfEachOther() {
        var result = Types.isSubType(Types.BOOL, Types.INT);

        assertThat(result, equalTo(false));
    }

    @Test
    public void allTypesAreSubTypeOfObject() {
        var result = Types.isSubType(Types.BOOL, Types.OBJECT);

        assertThat(result, equalTo(true));
    }
}
