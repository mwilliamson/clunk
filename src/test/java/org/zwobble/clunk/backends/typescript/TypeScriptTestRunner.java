package org.zwobble.clunk.backends.typescript;

import org.zwobble.clunk.backends.TargetTestRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.testing.ProjectRoot.findRoot;
import static org.zwobble.clunk.util.DeleteRecursive.deleteRecursive;

public class TypeScriptTestRunner implements TargetTestRunner {
    @Override
    public String runTests(Path outputRoot) throws IOException, InterruptedException {
        var typeScriptRoot = findRoot().resolve("testing/typescript");
        var nodeModules = typeScriptRoot.resolve("node_modules");

        var srcPath = typeScriptRoot.resolve("src");
        if (srcPath.toFile().exists()) {
            deleteRecursive(srcPath);
        }
        var copyResult = new ProcessBuilder("cp", "-r", outputRoot.toString(), srcPath.toString())
            .start()
            .waitFor();
        assertThat(copyResult, equalTo(0));

        var processBuilder = new ProcessBuilder(
            nodeModules.resolve(".bin/mocha").toString(),
            // TODO: escaping
            srcPath + "/**/*Test.ts",
            "--require", "ts-node/register",
            "--ui", "tdd"
        )
            .directory(typeScriptRoot.toFile())
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
