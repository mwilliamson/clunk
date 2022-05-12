package org.zwobble.clunk.backends;

import java.io.IOException;
import java.nio.file.Path;

public interface TargetTestRunner {
    String runTests(Path outputRoot) throws IOException, InterruptedException;
}
