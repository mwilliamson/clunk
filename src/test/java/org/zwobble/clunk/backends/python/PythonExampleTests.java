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

@ExtendWith(SnapshotResolver.class)
public class PythonExampleTests {
    @Test
    public void simpleTest(Snapshotter snapshotter) throws IOException, InterruptedException {
        var sourcePath = findRoot().resolve("examples/SimpleTest.clunk");

        var temporaryDirectory = Files.createTempDirectory("clunk-tests");
        try {
            var outputPath = temporaryDirectory.resolve("SimpleTest.py");
            var compiler = new Compiler();
            compiler.compile(sourcePath, outputPath, new PythonBackend());
            var virtualenvPath = findRoot().resolve("testing/python/_virtualenv");

            var process = new ProcessBuilder(
                virtualenvPath.resolve("bin/py.test").toString(),
                "--tb=short",
                outputPath.toString()
            )
                .directory(temporaryDirectory.toFile())
                .start();

            var output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                .lines()
                .filter(line -> !Pattern.matches("^platform [a-z]+ -- Python.*", line))
                .map(
                    line -> line
                        .replace(temporaryDirectory.toString(), "ROOTDIR")
                        .replaceAll(Pattern.quote(virtualenvPath.toString()) + ".*site-packages", "SITE-PACKAGES")
                        .replaceAll("in [0-9.]+s =======", "in TIME =======")
                )
                .collect(Collectors.joining("\n"));

            process.waitFor();

            var separator = "\n\n==============\n\n";

            snapshotter.assertSnapshot(
                Files.readString(sourcePath) + separator +
                    Files.readString(outputPath) + separator +
                    output
            );
        } finally {
            deleteRecursive(temporaryDirectory);
        }
    }

    private static void deleteRecursive(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(
                Path file,
                BasicFileAttributes attrs
            ) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(
                Path dir,
                IOException exc
            ) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
