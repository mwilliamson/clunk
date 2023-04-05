package org.zwobble.clunk.types;

import org.pcollections.PMap;
import org.pcollections.PVector;
import org.zwobble.clunk.typechecker.TypeConstraintSolver;
import org.zwobble.clunk.util.P;

import java.util.List;

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
        var typeConstraintSolver = new TypeConstraintSolver(List.of(), this);
        return typeConstraintSolver.addSubtypeConstraint(subtype, supertype);
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
