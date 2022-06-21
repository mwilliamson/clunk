package org.zwobble.clunk.backends.typescript;

import org.junit.jupiter.api.extension.ExtendWith;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.ExampleTests;
import org.zwobble.clunk.backends.TargetTestRunner;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.testing.snapshots.SnapshotResolver;

@ExtendWith(SnapshotResolver.class)
public class TypeScriptExampleTests extends ExampleTests {
    @Override
    protected Backend createBackend(Logger logger) {
        return new TypeScriptBackend(logger);
    }

    @Override
    protected TargetTestRunner targetTestRunner() {
        return new TypeScriptTestRunner();
    }
}
