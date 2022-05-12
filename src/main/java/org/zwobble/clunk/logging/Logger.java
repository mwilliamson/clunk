package org.zwobble.clunk.logging;

import java.nio.file.Path;

public interface Logger {
    Logger NULL = new NullLogger();

    void sourceFile(Path path, String contents);
    void outputFile(Path path, String contents);
}
