package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.Type;

public record SubtypeRelation(RecordType subtype, Type supertype) {
}
