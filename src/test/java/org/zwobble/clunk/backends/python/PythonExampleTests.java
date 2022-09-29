package org.zwobble.clunk.backends.python;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zwobble.clunk.backends.Backend;
import org.zwobble.clunk.backends.ExampleTests;
import org.zwobble.clunk.backends.TargetTestRunner;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.testing.snapshots.SnapshotResolver;
import org.zwobble.clunk.testing.snapshots.Snapshotter;

import java.io.IOException;

@ExtendWith(SnapshotResolver.class)
public class PythonExampleTests extends ExampleTests {
    @Override
    protected Backend createBackend(Logger logger) {
        return new PythonBackend(logger);
    }

    @Override
    protected TargetTestRunner targetTestRunner() {
        return new PythonTestRunner();
    }

    @Test
    public void testSuites(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "testSuites"
        );
    }
}
