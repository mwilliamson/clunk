package org.zwobble.clunk.backends.java;

import org.junit.jupiter.api.extension.ExtendWith;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.ExampleTests;
import org.zwobble.clunk.backends.TargetTestRunner;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.testing.snapshots.SnapshotResolver;

@ExtendWith(SnapshotResolver.class)
public class JavaExampleTests extends ExampleTests {
    @Override
    protected Backend createBackend(Logger logger) {
        return new JavaBackend(logger);
    }

    @Override
    protected TargetTestRunner targetTestRunner() {
        return new JavaTestRunner();
    }
}
