package org.zwobble.clunk.testing.snapshots;

import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.testing.ProjectRoot.findRoot;

public class Snapshotter {
    private final String uniqueId;

    public Snapshotter(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void assertSnapshot(String actualSnapshot) throws IOException {
        var snapshotDirectory = findRoot().resolve("snapshots");

        var expectedSnapshotPath = snapshotDirectory.resolve(uniqueId + ".expected");
        var actualSnapshotPath = snapshotDirectory.resolve(uniqueId + ".actual");
        var actualSnapshotFile = actualSnapshotPath.toFile();

        try {
            if (Files.exists(expectedSnapshotPath)) {
                var expectedSnapshot = Files.readString(expectedSnapshotPath);
                assertThat(actualSnapshot, equalTo(expectedSnapshot));
                if (actualSnapshotFile.exists()) {
                    actualSnapshotFile.delete();
                }
            } else {
                throw new AssertionError("snapshot does not exist, got:\n" + actualSnapshot);
            }
        } catch (AssertionError error) {
            Files.createDirectories(actualSnapshotPath.getParent());
            Files.writeString(actualSnapshotPath, actualSnapshot);
            throw error;
        }
    }
}
