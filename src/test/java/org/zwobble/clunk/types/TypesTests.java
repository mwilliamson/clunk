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

    @Test
    public void whenRecordIsExplicitSubtypeOfInterfaceThenRecordIsSubtypeOfInterface() {
        var interfaceType = Types.interfaceType(NamespaceName.fromParts(), "Node");
        var recordType = Types.recordType(NamespaceName.fromParts(), "Add");
        var subtypeRelations = SubtypeRelations.EMPTY
            .add(recordType, interfaceType);

        var result = subtypeRelations.isSubType(recordType, interfaceType);

        assertThat(result, equalTo(true));
    }

    @Test
    public void whenRecordIsUnrelatedToInterfaceThenRecordIsNotSubtypeOfInterface() {
        var unrelatedInterfaceType = Types.interfaceType(NamespaceName.fromParts(), "Node");
        var unrelatedRecordType = Types.recordType(NamespaceName.fromParts(), "Add");
        var recordType = Types.recordType(NamespaceName.fromParts(), "User");
        var subtypeRelations = SubtypeRelations.EMPTY
            .add(unrelatedRecordType, unrelatedInterfaceType);

        var result = subtypeRelations.isSubType(recordType, unrelatedInterfaceType);

        assertThat(result, equalTo(false));
    }
}
