package org.zwobble.clunk.logging;

import java.nio.file.Path;

public class NullLogger implements Logger {
    @Override
    public void sourceFile(Path sourcePath, String contents) {
    }
}
