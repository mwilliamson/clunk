package org.zwobble.clunk.backends.java;

import org.zwobble.clunk.backends.TargetTestRunner;

import java.io.IOException;
import java.nio.file.Path;

public class JavaTestRunner implements TargetTestRunner {
    @Override
    public String runTests(Path outputRoot) throws IOException, InterruptedException {
        return "";
    }
}
