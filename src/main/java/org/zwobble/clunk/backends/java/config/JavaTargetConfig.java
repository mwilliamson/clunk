package org.zwobble.clunk.backends.java.config;

import com.electronwill.nightconfig.core.Config;

public class JavaTargetConfig {
    public static JavaTargetConfig stub() {
        return new JavaTargetConfig(Config.inMemory());
    }

    private final Config config;

    public JavaTargetConfig(Config config) {
        this.config = config;
    }

    public String packagePrefix() {
        // TODO: handle bad value
        return config.getOrElse("package-prefix", "");
    }
}
