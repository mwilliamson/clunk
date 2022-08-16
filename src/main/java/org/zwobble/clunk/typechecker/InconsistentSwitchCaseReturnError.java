package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;

public class InconsistentSwitchCaseReturnError extends SourceError {
    public InconsistentSwitchCaseReturnError(Source source) {
        super("A switch statement must either return from all cases, or return from none", source);
    }
}
