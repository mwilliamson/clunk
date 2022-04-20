package org.zwobble.clunk.backends.python.codegenerator;

import java.util.Locale;

public class PythonTestNames {
    public static String generateName(String name) {
        return name.toLowerCase(Locale.ROOT).replace(" ", "_");
    }
}
