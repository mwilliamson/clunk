package org.zwobble.clunk.types;

import org.pcollections.PMap;
import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

public class SubtypeRelations {
    public static final SubtypeRelations EMPTY = new SubtypeRelations(P.map(), P.map());

    private final PMap<Type, PVector<StructuredType>> supertypeToSubtypes;
    private final PMap<Type, PVector<StructuredType>> subtypesToSupertypes;

    private SubtypeRelations(
        PMap<Type, PVector<StructuredType>> supertypeToSubtypes,
        PMap<Type, PVector<StructuredType>> subtypesToSupertypes
    ) {
        this.supertypeToSubtypes = supertypeToSubtypes;
        this.subtypesToSupertypes = subtypesToSupertypes;
    }

    public PVector<StructuredType> subtypesOf(Type supertype) {
        return supertypeToSubtypes.getOrDefault(supertype, P.vector());
    }

    public PVector<StructuredType> supertypesOf(Type subtype) {
        return subtypesToSupertypes.getOrDefault(subtype, P.vector());
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

        var explicitSupertypes = supertypesOf(subtype);
        if (explicitSupertypes.contains(supertype)) {
            return true;
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

    public SubtypeRelations add(StructuredType subtype, StructuredType supertype) {
        var subtypesToSupertypes = this.subtypesToSupertypes.plus(
            subtype,
            supertypesOf(subtype).plus(supertype)
        );
        var supertypeToSubtypes = this.supertypeToSubtypes.plus(
            supertype,
            subtypesOf(supertype).plus(subtype)
        );
        return new SubtypeRelations(supertypeToSubtypes, subtypesToSupertypes);
    }
}
