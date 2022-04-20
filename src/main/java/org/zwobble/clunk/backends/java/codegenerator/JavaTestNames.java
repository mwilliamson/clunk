package org.zwobble.clunk.backends.java.codegenerator;

import java.util.Locale;
import java.util.regex.Pattern;

public class JavaTestNames {
    public static String generateName(String name) {
        return Pattern.compile(" ([a-z])")
            .matcher(name)
            .replaceAll(result -> result.group(1).toUpperCase(Locale.ROOT));
    }
}
