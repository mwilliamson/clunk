package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.errors.InternalCompilerError;

import java.util.Locale;
import java.util.regex.Pattern;

public class JavaTestNames {
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^[a-z][A-Za-z0-9]*$");

    public static String generateName(String name) {
        var identifier = Pattern.compile(" ([a-z])")
            .matcher(name.replace(" == ", " equals "))
            .replaceAll(result -> result.group(1).toUpperCase(Locale.ROOT));

        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new InternalCompilerError("Could not convert test name to Java identifier: " + name);
        }

        return identifier;
    }
}
