package org.zwobble.clunk.backends.python;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zwobble.clunk.Compiler;
import org.zwobble.clunk.backends.TargetTestRunner;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.testing.snapshots.SnapshotResolver;
import org.zwobble.clunk.testing.snapshots.Snapshotter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.zwobble.clunk.testing.ProjectRoot.findRoot;
import static org.zwobble.clunk.util.DeleteRecursive.deleteRecursive;

@ExtendWith(SnapshotResolver.class)
public class PythonExampleTests {
    @Test
    public void simpleTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "SimpleTest",
            new PythonTestRunner()
        );
    }

    @Test
    public void nestedNamespaceTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "NestedNamespaceTest",
            new PythonTestRunner()
        );
    }

    private void runExampleTest(
        Snapshotter snapshotter,
        String exampleName,
        TargetTestRunner targetTestRunner
    ) throws IOException, InterruptedException {
        var sourceRoot = findRoot().resolve("examples/" + exampleName);

        var outputRoot = Files.createTempDirectory("clunk-tests");
        try {
            var snapshot = new StringBuilder();
            var separator = "\n\n==============\n\n";

            var logger = new Logger() {
                @Override
                public void sourceFile(Path path, String contents) {
                    logFile("Source", sourceRoot.relativize(path), contents);
                }

                @Override
                public void outputFile(Path path, String contents) {
                    logFile("Output", outputRoot.relativize(path), contents);
                }

                private void logFile(String fileType, Path path, String contents) {
                    snapshot.append(fileType + " path: " + path + "\n");
                    snapshot.append(contents);
                    snapshot.append(separator);
                }
            };
            var compiler = new Compiler(logger);
            compiler.compile(sourceRoot, outputRoot, new PythonBackend(logger));

            var output = targetTestRunner.runTests(outputRoot);

            snapshot.append(output);

            snapshotter.assertSnapshot(snapshot.toString());
        } finally {
            deleteRecursive(outputRoot);
        }
    }
}
