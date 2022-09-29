package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.errors.InternalCompilerError;

import java.util.regex.Pattern;

import static org.zwobble.clunk.backends.TestNameConverter.testNameToLowerCamelCase;

public class JavaTestNames {
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^[a-z][A-Za-z0-9]*$");

    public static String generateName(String name) {
        var identifier = testNameToLowerCamelCase(name);

        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new InternalCompilerError("Could not convert test name to Java identifier: " + name);
        }

        return identifier;
    }
}
