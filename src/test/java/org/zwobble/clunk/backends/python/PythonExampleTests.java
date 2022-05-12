package org.zwobble.clunk.backends.python;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zwobble.clunk.Compiler;
import org.zwobble.clunk.testing.snapshots.SnapshotResolver;
import org.zwobble.clunk.testing.snapshots.Snapshotter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
            "SimpleTest.py"
        );
    }

    @Test
    public void nestedNamespaceTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        runExampleTest(
            snapshotter,
            "NestedNamespaceTest",
            "One/Two/SimpleTest.py"
        );
    }

    private void runExampleTest(Snapshotter snapshotter, String exampleName, String outputPath) throws IOException, InterruptedException {
        var sourceRoot = findRoot().resolve("examples/" + exampleName);

        var outputRoot = Files.createTempDirectory("clunk-tests");
        try {
            var snapshot = new StringBuilder();
            var separator = "\n\n==============\n\n";

            var compiler = new Compiler(new Compiler.Logger() {
                @Override
                public void sourceFile(Path sourcePath, String contents) {
                    snapshot.append("Source path: " + sourceRoot.relativize(sourcePath) + "\n");
                    snapshot.append(contents);
                    snapshot.append(separator);
                }
            });
            compiler.compile(sourceRoot, outputRoot, new PythonBackend());
            var virtualenvPath = findRoot().resolve("testing/python/_virtualenv");

            var process = new ProcessBuilder(
                virtualenvPath.resolve("bin/py.test").toString(),
                "--tb=short",
                outputRoot.resolve(outputPath).toString()
            )
                .directory(outputRoot.toFile())
                .start();

            var output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .filter(line -> !Pattern.matches("^platform [a-z]+ -- Python.*", line))
                .map(
                    line -> line
                        .replace(outputRoot.toString(), "ROOTDIR")
                        .replaceAll(Pattern.quote(virtualenvPath.toString()) + ".*site-packages", "SITE-PACKAGES")
                        .replaceAll("in [0-9.]+s =======", "in TIME =======")
                )
                .collect(Collectors.joining("\n"));

            process.waitFor();

            snapshot.append(Files.readString(outputRoot.resolve(outputPath)));
            snapshot.append(separator);
            snapshot.append(output);

            snapshotter.assertSnapshot(snapshot.toString());
        } finally {
            deleteRecursive(outputRoot);
        }
    }
}
