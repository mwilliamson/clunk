package org.zwobble.clunk.types;

import org.pcollections.PMap;
import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

public class SubtypeRelations {
    public static final SubtypeRelations EMPTY = new SubtypeRelations(P.map(), P.map());

    private final PMap<InterfaceType, PVector<RecordType>> sealedInterfaceToCases;
    private final PMap<Type, PVector<StructuredType>> typeToExtendedTypes;

    private SubtypeRelations(
        PMap<InterfaceType, PVector<RecordType>> sealedInterfaceCases,
        PMap<Type, PVector<StructuredType>> typeToExtendedTypes
    ) {
        this.sealedInterfaceToCases = sealedInterfaceCases;
        this.typeToExtendedTypes = typeToExtendedTypes;
    }

    public PVector<RecordType> sealedInterfaceCases(InterfaceType sealedInterfaceType) {
        return sealedInterfaceToCases.getOrDefault(sealedInterfaceType, P.vector());
    }

    public PVector<StructuredType> extendedTypes(Type subtype) {
        if (subtype instanceof ConstructedType subtypeConstructed) {
            var genericType = subtypeConstructed.constructor().genericType();
            // TODO: find a better way of converting to PVector (or just use List)
            return P.copyOf(extendedTypes(genericType).stream()
                .map(extendedType -> extendedType.replace(subtypeConstructed.typeMap()))
                .toList());
        } else {
            return typeToExtendedTypes.getOrDefault(subtype, P.vector());
        }
    }

    public boolean isSubType(Type subtype, Type supertype) {
        if (supertype.equals(Types.OBJECT)) {
            return true;
        }

        if (subtype.equals(Types.NOTHING)) {
            return true;
        }

        if (subtype.equals(supertype)) {
            return true;
        }

        var extendedTypes = extendedTypes(subtype);
        for (var extendedType : extendedTypes) {
            if (isSubType(extendedType, supertype)) {
                return true;
            }
        }

        if (
            subtype instanceof FunctionType subtypeFunction &&
            supertype instanceof FunctionType supertypeFunction
        ) {
            if (subtypeFunction.positionalParams().size() != supertypeFunction.positionalParams().size()) {
                return false;
            }

            for (var i = 0; i < subtypeFunction.positionalParams().size(); i++) {
                var subtypeParam = subtypeFunction.positionalParams().get(i);
                var supertypeParam = supertypeFunction.positionalParams().get(i);
                if (!isSubType(supertypeParam, subtypeParam)) {
                    return false;
                }
            }

            if (!isSubType(subtypeFunction.returnType(), supertypeFunction.returnType())) {
                return false;
            }

            return true;
        }

        if (
            subtype instanceof ConstructedType subtypeConstructed &&
            supertype instanceof ConstructedType supertypeConstructed &&
            subtypeConstructed.constructor().equals(supertypeConstructed.constructor())
        ) {
            for (var i = 0; i < subtypeConstructed.constructor().params().size(); i++) {
                var param = subtypeConstructed.constructor().params().get(i);
                var argSubtype = subtypeConstructed.args().get(0);
                var argSupertype = supertypeConstructed.args().get(0);

                switch (param.variance()) {
                    case COVARIANT -> {
                        if (!isSubType(argSubtype, argSupertype)) {
                            return false;
                        }
                    }
                    case INVARIANT -> {
                        if (!argSubtype.equals(argSupertype)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }

        return false;
    }

    public SubtypeRelations addExtendedType(StructuredType subtype, StructuredType supertype) {
        var subtypeToSupertypes = this.typeToExtendedTypes.plus(
            subtype,
            extendedTypes(subtype).plus(supertype)
        );
        return new SubtypeRelations(sealedInterfaceToCases, subtypeToSupertypes);
    }

    public SubtypeRelations addSealedInterfaceCase(InterfaceType sealedInterfaceType, RecordType caseType) {
        var sealedInterfaceCases = this.sealedInterfaceToCases.plus(
            sealedInterfaceType,
            sealedInterfaceCases(sealedInterfaceType).plus(caseType)
        );
        return new SubtypeRelations(sealedInterfaceCases, typeToExtendedTypes);

    }
}
