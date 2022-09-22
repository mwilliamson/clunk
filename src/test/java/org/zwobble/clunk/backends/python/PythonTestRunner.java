package org.zwobble.clunk.backends.python;

import org.zwobble.clunk.backends.TargetTestRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.zwobble.clunk.testing.ProjectRoot.findRoot;

public class PythonTestRunner implements TargetTestRunner {
    @Override
    public String runTests(Path outputRoot) throws IOException, InterruptedException {
        var virtualenvPath = findRoot().resolve("testing/python/_virtualenv");

        var processBuilder = new ProcessBuilder(
            virtualenvPath.resolve("bin/py.test").toString(),
            "--tb=short",
            outputRoot.toString()
        )
            .directory(outputRoot.toFile());

        processBuilder.environment().put("PYTHONPATH", ".");

        var process = processBuilder.start();

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
        return output;
    }
}
