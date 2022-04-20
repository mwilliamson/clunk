package org.zwobble.clunk.backends.python.codegenerator;

public class PythonTestNames {
    public static String generateName(String name) {
        return name.replace(" ", "_");
    }
}
