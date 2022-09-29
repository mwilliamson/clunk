package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.errors.InternalCompilerError;

import java.util.regex.Pattern;

import static org.zwobble.clunk.backends.TestNameConverter.testNameToLowerCamelCase;
import static org.zwobble.clunk.backends.TestNameConverter.testNameToUpperCamelCase;

public class JavaTestNames {
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^[A-Za-z][A-Za-z0-9]*$");

    public static String generateTestName(String name) {
        var identifier = testNameToLowerCamelCase(name);

        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new InternalCompilerError("Could not convert test name to Java identifier: " + name);
        }

        return identifier;
    }

    public static String generateTestSuiteName(String name) {
        var identifier = testNameToUpperCamelCase(name);

        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new InternalCompilerError("Could not convert test suite name to Java identifier: " + name);
        }

        return identifier;
    }
}
