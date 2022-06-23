package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubtypeLookup {
    public static final SubtypeLookup EMPTY = new SubtypeLookup(Map.of(), Map.of());

    public static SubtypeLookup fromSubtypeRelations(List<SubtypeRelation> subtypeRelations) {
        var supertypeToSubtypes = new HashMap<Type, List<RecordType>>();
        var subtypesToSupertypes = new HashMap<Type, List<Type>>();

        for (var relation : subtypeRelations) {
            if (!supertypeToSubtypes.containsKey(relation.supertype())) {
                supertypeToSubtypes.put(relation.supertype(), new ArrayList<>());
            }
            supertypeToSubtypes.get(relation.supertype()).add(relation.subtype());

            if (!subtypesToSupertypes.containsKey(relation.subtype())) {
                subtypesToSupertypes.put(relation.subtype(), new ArrayList<>());
            }
            subtypesToSupertypes.get(relation.subtype()).add(relation.supertype());
        }

        return new SubtypeLookup(supertypeToSubtypes, subtypesToSupertypes);
    }

    private final Map<Type, List<RecordType>> supertypeToSubtypes;
    private final Map<Type, List<Type>> subtypesToSupertypes;

    private SubtypeLookup(
        Map<Type, List<RecordType>> supertypeToSubtypes,
        Map<Type, List<Type>> subtypesToSupertypes
    ) {
        this.supertypeToSubtypes = supertypeToSubtypes;
        this.subtypesToSupertypes = subtypesToSupertypes;
    }

    public List<RecordType> subtypesOf(Type supertype) {
        return supertypeToSubtypes.getOrDefault(supertype, List.of());
    }

    public List<Type> supertypesOf(Type subtype) {
        return subtypesToSupertypes.getOrDefault(subtype, List.of());
    }
}
