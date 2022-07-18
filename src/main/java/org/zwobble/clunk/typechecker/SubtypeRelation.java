package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.RecordType;

public record SubtypeRelation(RecordType subtype, InterfaceType supertype) {
}
