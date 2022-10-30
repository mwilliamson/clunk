package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TypesTests {
    private static final TypeConstructor TYPE_CONSTRUCTOR_COVARIANT = new TypeConstructor(
        "TypeConstructorCovariant",
        List.of(TypeParameter.covariant(NamespaceName.fromParts(), "TypeConstructorCovariant", "T")),
        Types.recordType(NamespaceName.fromParts(), "TypeConstructorCovariant")
    );

    private static final TypeConstructor TYPE_CONSTRUCTOR_COVARIANT_OTHER = new TypeConstructor(
        "TypeConstructorCovariantOther",
        List.of(TypeParameter.covariant(NamespaceName.fromParts(), "TypeConstructorCovariantOther", "T")),
        Types.recordType(NamespaceName.fromParts(), "TypeConstructorCovariantOther")
    );

    private static final TypeConstructor TYPE_CONSTRUCTOR_INVARIANT = new TypeConstructor(
        "TypeConstructorInvariant",
        List.of(TypeParameter.invariant(NamespaceName.fromParts(), "TypeConstructorInvariant", "T")),
        Types.recordType(NamespaceName.fromParts(), "TypeConstructorInvariant")
    );

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
    public void allTypesAreSupertypeOfNothing() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(Types.NOTHING, Types.BOOL);

        assertThat(result, equalTo(true));
    }

    @Test
    public void whenRecordIsExplicitSubtypeOfInterfaceThenRecordIsSubtypeOfInterface() {
        var interfaceType = Types.interfaceType(NamespaceName.fromParts(), "Node");
        var recordType = Types.recordType(NamespaceName.fromParts(), "Add");
        var subtypeRelations = SubtypeRelations.EMPTY
            .addExtendedType(recordType, interfaceType);

        var result = subtypeRelations.isSubType(recordType, interfaceType);

        assertThat(result, equalTo(true));
    }

    @Test
    public void subtypingIsTransitive() {
        var type = Types.interfaceType(NamespaceName.fromParts(), "Type");
        var superType = Types.interfaceType(NamespaceName.fromParts(), "SuperType");
        var superSuperType = Types.interfaceType(NamespaceName.fromParts(), "SuperSuperType");
        var subtypeRelations = SubtypeRelations.EMPTY
            .addExtendedType(type, superType)
            .addExtendedType(superType, superSuperType);

        var result = subtypeRelations.isSubType(type, superSuperType);

        assertThat(result, equalTo(true));
    }

    @Test
    public void whenRecordIsUnrelatedToInterfaceThenRecordIsNotSubtypeOfInterface() {
        var unrelatedInterfaceType = Types.interfaceType(NamespaceName.fromParts(), "Node");
        var unrelatedRecordType = Types.recordType(NamespaceName.fromParts(), "Add");
        var recordType = Types.recordType(NamespaceName.fromParts(), "User");
        var subtypeRelations = SubtypeRelations.EMPTY
            .addExtendedType(unrelatedRecordType, unrelatedInterfaceType);

        var result = subtypeRelations.isSubType(recordType, unrelatedInterfaceType);

        assertThat(result, equalTo(false));
    }

    @Test
    public void functionIsNotSubtypeOfFunctionWithMoreParams() {
        var supertype = Types.functionType(List.of(Types.OBJECT), Types.OBJECT);
        var subtype = Types.functionType(List.of(), Types.OBJECT);
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(subtype, supertype);

        assertThat(result, equalTo(false));
    }

    @Test
    public void functionIsNotSubtypeOfFunctionWithFewerParams() {
        var supertype = Types.functionType(List.of(), Types.OBJECT);
        var subtype = Types.functionType(List.of(Types.OBJECT), Types.OBJECT);
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(subtype, supertype);

        assertThat(result, equalTo(false));
    }

    @Test
    public void functionParamTypesAreContravariant() {
        var supertype = Types.functionType(List.of(Types.STRING), Types.OBJECT);
        var subtype = Types.functionType(List.of(Types.OBJECT), Types.OBJECT);
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(subtype, supertype);

        assertThat(result, equalTo(true));
    }

    @Test
    public void functionParamTypesAreNotCovariant() {
        var supertype = Types.functionType(List.of(Types.OBJECT), Types.OBJECT);
        var subtype = Types.functionType(List.of(Types.STRING), Types.OBJECT);
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(subtype, supertype);

        assertThat(result, equalTo(false));
    }

    @Test
    public void functionReturnTypesAreCovariant() {
        var supertype = Types.functionType(List.of(), Types.OBJECT);
        var subtype = Types.functionType(List.of(), Types.INT);
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(subtype, supertype);

        assertThat(result, equalTo(true));
    }

    @Test
    public void functionReturnTypesAreNotContravariant() {
        var supertype = Types.functionType(List.of(), Types.OBJECT);
        var subtype = Types.functionType(List.of(), Types.INT);
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(supertype, subtype);

        assertThat(result, equalTo(false));
    }

    @Test
    public void constructedTypesWithUnrelatedConstructorsAreNotSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.STRING)),
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT_OTHER, List.of(Types.STRING))
        );

        assertThat(result, equalTo(false));
    }

    @Test
    public void givenTypeParamIsInvariantWhenTypeArgsAreTheSameThenConstructedTypesAreSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.STRING)),
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.STRING))
        );

        assertThat(result, equalTo(true));
    }

    @Test
    public void givenTypeParamIsInvariantWhenTypeArgsAreUnrelatedThenConstructedTypesAreNotSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.STRING)),
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.INT))
        );

        assertThat(result, equalTo(false));
    }

    @Test
    public void givenTypeParamIsInvariantWhenTypeArgsAreSubtypesThenConstructedTypesAreNotSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.STRING)),
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.OBJECT))
        );

        assertThat(result, equalTo(false));
    }

    @Test
    public void givenTypeParamIsInvariantWhenTypeArgsAreSupertypesThenConstructedTypesAreNotSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.OBJECT)),
            Types.construct(TYPE_CONSTRUCTOR_INVARIANT, List.of(Types.STRING))
        );

        assertThat(result, equalTo(false));
    }

    @Test
    public void givenTypeParamIsCovariantWhenTypeArgsAreTheSameThenConstructedTypesAreSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.STRING)),
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.STRING))
        );

        assertThat(result, equalTo(true));
    }

    @Test
    public void givenTypeParamIsCovariantWhenTypeArgsAreUnrelatedThenConstructedTypesAreNotSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.STRING)),
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.INT))
        );

        assertThat(result, equalTo(false));
    }

    @Test
    public void givenTypeParamIsCovariantWhenTypeArgsAreSubtypesThenConstructedTypesAreSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.STRING)),
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.OBJECT))
        );

        assertThat(result, equalTo(true));
    }

    @Test
    public void givenTypeParamIsCovariantWhenTypeArgsAreSupertypesThenConstructedTypesAreNotSubtypes() {
        var subtypeRelations = SubtypeRelations.EMPTY;

        var result = subtypeRelations.isSubType(
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.OBJECT)),
            Types.construct(TYPE_CONSTRUCTOR_COVARIANT, List.of(Types.STRING))
        );

        assertThat(result, equalTo(false));
    }
}
