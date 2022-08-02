package org.zwobble.clunk.types;

import org.pcollections.PMap;
import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

public class SubtypeRelations {
    public static final SubtypeRelations EMPTY = new SubtypeRelations(P.map(), P.map());

    private final PMap<Type, PVector<RecordType>> supertypeToSubtypes;
    private final PMap<Type, PVector<InterfaceType>> subtypesToSupertypes;

    private SubtypeRelations(
        PMap<Type, PVector<RecordType>> supertypeToSubtypes,
        PMap<Type, PVector<InterfaceType>> subtypesToSupertypes
    ) {
        this.supertypeToSubtypes = supertypeToSubtypes;
        this.subtypesToSupertypes = subtypesToSupertypes;
    }

    public PVector<RecordType> subtypesOf(Type supertype) {
        return supertypeToSubtypes.getOrDefault(supertype, P.vector());
    }

    public PVector<InterfaceType> supertypesOf(Type subtype) {
        return subtypesToSupertypes.getOrDefault(subtype, P.vector());
    }

    public boolean isSubType(Type subtype, Type supertype) {
        if (supertype.equals(Types.OBJECT)) {
            return true;
        }

        if (subtype.equals(supertype)) {
            return true;
        }

        var explicitSupertypes = supertypesOf(subtype);
        if (explicitSupertypes.contains(supertype)) {
            return true;
        }

        return false;
    }

    public SubtypeRelations add(RecordType subtype, InterfaceType supertype) {
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
