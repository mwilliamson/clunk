package org.zwobble.clunk.logging;

import java.nio.file.Path;

public class NullLogger implements Logger {
    @Override
    public void sourceFile(Path path, String contents) {
    }

    @Override
    public void outputFile(Path path, String contents) {
    }
}
