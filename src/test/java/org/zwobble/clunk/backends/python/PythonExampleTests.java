package org.zwobble.clunk.backends.python;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zwobble.clunk.testing.snapshots.SnapshotResolver;
import org.zwobble.clunk.testing.snapshots.Snapshotter;

import java.io.IOException;

import static org.zwobble.clunk.backends.ExampleTests.runExampleTest;

@ExtendWith(SnapshotResolver.class)
public class PythonExampleTests {
    @Test
    public void simpleTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runPythonExampleTest(
            snapshotter,
            "SimpleTest"
        );
    }

    @Test
    public void nestedNamespaceTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runPythonExampleTest(
            snapshotter,
            "NestedNamespaceTest"
        );
    }

    private void runPythonExampleTest(Snapshotter snapshotter, String testName) throws IOException, InterruptedException {
        runExampleTest(snapshotter, testName, logger -> new PythonBackend(logger), new PythonTestRunner());
    }
}
