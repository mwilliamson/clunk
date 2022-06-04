package org.zwobble.clunk.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.nio.file.Path;

public class ProjectConfig {
    public static ProjectConfig read(Path projectPath) {
        var config = FileConfig.builder(projectPath.resolve("clunk.toml"), TomlFormat.instance())
            .onFileNotFound(FileNotFoundAction.READ_NOTHING)
            .build();
        config.load();
        return new ProjectConfig(config);
    }

    private final Config config;

    public ProjectConfig(FileConfig config) {
        this.config = config;
    }

    public Config target(String targetName) {
        // TODO: handle not a config
        return config.getOrElse("targets." + targetName, Config.inMemory());
    }
}
