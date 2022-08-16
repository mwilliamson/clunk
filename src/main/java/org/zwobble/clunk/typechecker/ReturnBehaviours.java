package org.zwobble.clunk.typechecker;

import java.util.Collection;

public class ReturnBehaviours {
    private ReturnBehaviours() {
    }

    public static ReturnBehaviour or(Collection<ReturnBehaviour> returnBehaviours) {
        return returnBehaviours.stream()
            .reduce(ReturnBehaviours::or)
            .get();
    }

    public static ReturnBehaviour or(ReturnBehaviour left, ReturnBehaviour right) {
        if (left.equals(ReturnBehaviour.NEVER) && right.equals(ReturnBehaviour.NEVER)) {
            return ReturnBehaviour.NEVER;
        } else if (left.equals(ReturnBehaviour.ALWAYS) && right.equals(ReturnBehaviour.ALWAYS)) {
            return ReturnBehaviour.ALWAYS;
        } else {
            return ReturnBehaviour.SOMETIMES;
        }
    }
}
