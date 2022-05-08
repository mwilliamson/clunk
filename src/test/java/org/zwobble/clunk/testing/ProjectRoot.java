package org.zwobble.clunk.testing;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProjectRoot {
    public static Path findRoot() {
        var rootFilenames = List.of("examples", "src");
        var directory = Paths.get(System.getProperty("user.dir"));
        while (!Arrays.asList(Objects.requireNonNull(directory.toFile().list())).containsAll(rootFilenames)) {
            directory = directory.getParent();
        }
        return directory;
    }
}
