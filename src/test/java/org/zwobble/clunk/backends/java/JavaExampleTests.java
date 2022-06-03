package org.zwobble.clunk.backends.java;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zwobble.clunk.testing.snapshots.SnapshotResolver;
import org.zwobble.clunk.testing.snapshots.Snapshotter;

import java.io.IOException;

import static org.zwobble.clunk.backends.ExampleTests.runExampleTest;

@ExtendWith(SnapshotResolver.class)
public class JavaExampleTests {
    @Test
    public void simpleTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runJavaExampleTest(
            snapshotter,
            "SimpleTest"
        );
    }

    @Test
    public void nestedNamespaceTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runJavaExampleTest(
            snapshotter,
            "NestedNamespaceTest"
        );
    }

    private void runJavaExampleTest(Snapshotter snapshotter, String testName) throws IOException, InterruptedException {
        runExampleTest(snapshotter, testName, logger -> new JavaBackend(logger), new JavaTestRunner());
    }
}
