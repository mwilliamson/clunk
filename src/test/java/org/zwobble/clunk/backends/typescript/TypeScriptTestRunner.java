package org.zwobble.clunk.backends.typescript;

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

public class TypeScriptTestRunner implements TargetTestRunner {
    @Override
    public String runTests(Path outputRoot) throws IOException, InterruptedException {
        var typeScriptTestingRoot = findRoot().resolve("testing/typescript");
        var nodeModules = typeScriptTestingRoot.resolve("node_modules");

        var srcPath = typeScriptTestingRoot.resolve("src");
        if (srcPath.toFile().exists()) {
            deleteRecursive(srcPath);
        }

        var copyExitCode = new ProcessBuilder("cp", "-r", outputRoot.toString(), srcPath.toString())
            .start()
            .waitFor();
        assertThat(copyExitCode, equalTo(0));

        var processBuilder = new ProcessBuilder(
            nodeModules.resolve(".bin/mocha").toString(),
            // TODO: escaping
            "src/**/*.test.ts",
            "--require", "ts-node/register",
            "--ui", "tdd"
        )
            .directory(typeScriptTestingRoot.toFile())
            .redirectErrorStream(true);

        processBuilder.environment().put("NODE_PATH", nodeModules.toString());

        var process = processBuilder
            .start();

        var output = new BufferedReader(new InputStreamReader(process.getInputStream()))
            .lines()
            .map(
                line -> line.replaceAll("\\([0-9.]+ms\\)", "(TIME)")
            )
            .collect(Collectors.joining("\n"));

        process.waitFor();
        return output;
    }
}
