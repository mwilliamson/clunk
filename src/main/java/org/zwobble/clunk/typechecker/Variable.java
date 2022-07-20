package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.Type;

public record Variable(Type type, boolean isMember) {
    public static Variable local(Type type) {
        return new Variable(type, false);
    }

    public static Variable member(Type type) {
        return new Variable(type, true);
    }
}
