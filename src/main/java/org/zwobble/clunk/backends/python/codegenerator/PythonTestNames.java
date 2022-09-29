package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.errors.InternalCompilerError;

import java.util.regex.Pattern;

import static org.zwobble.clunk.backends.TestNameConverter.testNameToSnakeCase;
import static org.zwobble.clunk.backends.TestNameConverter.testNameToUpperCamelCase;

public class PythonTestNames {
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("^[_A-Za-z][_A-Za-z0-9]*$");

    public static String generateTestFunctionName(String name) {
        var identifier = testNameToSnakeCase(name);

        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new InternalCompilerError("Could not convert test name to Python identifier: " + name);
        }

        return "test_" + identifier;
    }

    public static String generateTestClassName(String name) {
        var identifier = testNameToUpperCamelCase(name);

        if (!VALID_IDENTIFIER.matcher(identifier).matches()) {
            throw new InternalCompilerError("Could not convert test suite name to Python identifier: " + name);
        }

        return identifier + "Tests";
    }
}
