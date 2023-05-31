package org.zwobble.clunk.types;

import org.pcollections.PMap;
import org.pcollections.PVector;
import org.zwobble.clunk.typechecker.TypeConstraintSolver;
import org.zwobble.clunk.util.P;
import org.zwobble.clunk.util.PCollectors;

import java.util.List;

public class SubtypeRelations {
    public static final SubtypeRelations EMPTY = new SubtypeRelations(P.map(), P.map());

    private final PMap<StructuredType, PVector<StructuredType>> sealedInterfaceToCases;
    private final PMap<Type, PVector<StructuredType>> typeToExtendedTypes;

    private SubtypeRelations(
        PMap<StructuredType, PVector<StructuredType>> sealedInterfaceCases,
        PMap<Type, PVector<StructuredType>> typeToExtendedTypes
    ) {
        this.sealedInterfaceToCases = sealedInterfaceCases;
        this.typeToExtendedTypes = typeToExtendedTypes;
    }

    public PVector<StructuredType> sealedInterfaceCases(StructuredType sealedType) {
        if (sealedType instanceof ConstructedType sealedConstructedType) {
            var genericType = sealedConstructedType.constructor().genericType();
            var typeMap = sealedConstructedType.typeMap();
            return sealedInterfaceCases(genericType).stream()
                .map(caseType -> caseType.replace(typeMap))
                .collect(PCollectors.toVector());
        }

        return sealedInterfaceToCases.getOrDefault(sealedType, P.vector());
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

    public SubtypeRelations addSealedInterfaceCase(StructuredType sealedInterfaceType, StructuredType caseType) {
        var sealedInterfaceCases = this.sealedInterfaceToCases.plus(
            sealedInterfaceType,
            sealedInterfaceCases(sealedInterfaceType).plus(caseType)
        );
        return new SubtypeRelations(sealedInterfaceCases, typeToExtendedTypes);

    }
}
