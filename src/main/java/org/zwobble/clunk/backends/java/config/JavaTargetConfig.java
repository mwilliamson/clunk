package org.zwobble.clunk.backends.java.config;

import com.electronwill.nightconfig.core.Config;

public record JavaTargetConfig(
    String packagePrefix
) {
    public static JavaTargetConfig load(Config config) {
        // TODO: handle bad value
        var packagePrefix = config.getOrElse("package-prefix", "");

        return new JavaTargetConfig(packagePrefix);
    }

    public static JavaTargetConfig stub() {
        return new JavaTargetConfig("");
    }
}
