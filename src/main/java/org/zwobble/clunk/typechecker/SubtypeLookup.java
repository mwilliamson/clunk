package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubtypeLookup {
    public static final SubtypeLookup EMPTY = new SubtypeLookup(Map.of());

    public static SubtypeLookup fromSubtypeRelations(List<SubtypeRelation> subtypeRelations) {
        var supertypeToSubtypes = new HashMap<Type, List<RecordType>>();

        for (var relation : subtypeRelations) {
            if (!supertypeToSubtypes.containsKey(relation.supertype())) {
                supertypeToSubtypes.put(relation.supertype(), new ArrayList<>());
            }
            supertypeToSubtypes.get(relation.supertype()).add(relation.subtype());
        }

        return new SubtypeLookup(supertypeToSubtypes);
    }

    private final Map<Type, List<RecordType>> supertypeToSubtypes;

    public SubtypeLookup(Map<Type, List<RecordType>> supertypeToSubtypes) {
        this.supertypeToSubtypes = supertypeToSubtypes;
    }

    public List<RecordType> subtypesOf(Type supertype) {
        return supertypeToSubtypes.getOrDefault(supertype, List.of());
    }
}
