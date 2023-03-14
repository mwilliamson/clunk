package org.zwobble.clunk.backends.java;

import org.zwobble.clunk.backends.TargetTestRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.testing.ProjectRoot.findRoot;
import static org.zwobble.clunk.util.DeleteRecursive.deleteRecursive;

public class JavaTestRunner implements TargetTestRunner {
    @Override
    public String runTests(Path outputRoot) throws IOException, InterruptedException {
        var javaTestingRoot = findRoot().resolve("testing/java");

        var srcPath = javaTestingRoot.resolve("src");
        if (srcPath.toFile().exists()) {
            deleteRecursive(srcPath);
        }

        var binPath = javaTestingRoot.resolve("bin");
        if (binPath.toFile().exists()) {
            deleteRecursive(binPath);
        }

        var copyExitCode = new ProcessBuilder("cp", "-r", outputRoot.toString(), srcPath.toString())
            .start()
            .waitFor();
        assertThat(copyExitCode, equalTo(0));

        var compileProcess = new ProcessBuilder("ant", "compile")
            .directory(javaTestingRoot.toFile())
            .redirectErrorStream(true)
            .start();
        var compileOutput = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()))
            .lines()
            .collect(Collectors.joining("\n"));
        var compileExitCode = compileProcess.waitFor();
        if (compileExitCode != 0) {
            throw new AssertionError("ant failed with exit code " + compileExitCode + "\noutput:\n" + compileOutput);
        }

        var junitProcess = new ProcessBuilder(
            "java",
            "-jar", javaTestingRoot.resolve("lib/junit-platform-console-standalone-1.8.2.jar").toString(),
            "--disable-ansi-colors", "--disable-banner",
            "--class-path", javaTestingRoot.resolve("lib/precisely-0.1.1.jar").toString(),
            "--class-path", binPath.toString(),
            "--scan-classpath", binPath.toString()
        )
            .redirectErrorStream(true)
            .start();

        var junitOutput = new BufferedReader(new InputStreamReader(junitProcess.getInputStream()))
            .lines()
            .map(
                line -> line.replaceAll("Test run finished after ([0-9+]+) ms", "Test run finished after TIME ms")
            )
            .collect(Collectors.joining("\n"));

        junitProcess.waitFor();

        return junitOutput;
    }
}
