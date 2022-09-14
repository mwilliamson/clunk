package org.zwobble.clunk.backends;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.Compiler;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.logging.Logger;
import org.zwobble.clunk.testing.snapshots.Snapshotter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.zwobble.clunk.testing.ProjectRoot.findRoot;
import static org.zwobble.clunk.util.DeleteRecursive.deleteRecursive;

public abstract class ExampleTests {
    @Test
    public void simpleTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "SimpleTest"
        );
    }

    @Test
    public void nestedNamespaceTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "NestedNamespaceTest"
        );
    }

    @Test
    public void namespaceConfig(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "NamespaceConfig"
        );
    }

    @Test
    public void varStatements(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "varStatements"
        );
    }

    @Test
    public void records(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "records"
        );
    }

    @Test
    public void stringBuilder(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "stringBuilder"
        );
    }

    @Test
    public void switches(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "switches"
        );
    }

    @Test
    public void mammoth(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "mammoth"
        );
    }

    protected abstract Backend createBackend(Logger logger);
    protected abstract TargetTestRunner targetTestRunner();

    protected void runExampleTest(
        Snapshotter snapshotter,
        String exampleName
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
            compiler.compile(sourceRoot, outputRoot, createBackend(logger));

            var output = targetTestRunner().runTests(outputRoot);

            snapshot.append(output);

            snapshotter.assertSnapshot(snapshot.toString());
        } catch (SourceError error) {
            throw new AssertionError(
                error.getMessage() + "\n" + error.getSource().describe()
            );
        } finally {
            deleteRecursive(outputRoot);
        }
    }
}
