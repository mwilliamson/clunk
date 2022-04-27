package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.errors.InternalCompilerError;

import java.util.Locale;
import java.util.regex.Pattern;

public class PythonTestNames {
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^[_A-Za-z][_A-Za-z0-9]*$");

    public static String generateName(String name) {
        var identifier = name.toLowerCase(Locale.ROOT)
            .replace(" == ", " equals ")
            .replace(" ", "_");

        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new InternalCompilerError("Could not convert test name to Python identifier: " + name);
        }

        return "test_" + identifier;
    }
}
