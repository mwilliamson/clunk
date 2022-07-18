package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public class UnknownMemberError extends SourceError {
    private final Type type;
    private final String memberName;

    public UnknownMemberError(Type type, String memberName, Source source) {
        super("Unknown member " + memberName + " on " + type.describe(), source);
        this.type = type;
        this.memberName = memberName;
    }

    public Type getType() {
        return type;
    }

    public String getMemberName() {
        return memberName;
    }
}
