package org.zwobble.clunk.backends;

import java.util.Locale;
import java.util.regex.Pattern;

import static org.zwobble.clunk.backends.CaseConverter.lowerCamelCaseToUpperCamelCase;

public class TestNameConverter {
    private TestNameConverter() {
    }

    public static String testNameToLowerCamelCase(String name) {
        return Pattern.compile(" (\\S)")
            .matcher(normalizeTestName(name))
            .replaceAll(result -> result.group(1).toUpperCase(Locale.ROOT));
    }

    public static String testNameToUpperCamelCase(String name) {
        return lowerCamelCaseToUpperCamelCase(testNameToLowerCamelCase(name));
    }

    public static String testNameToSnakeCase(String name) {
        return normalizeTestName(name).toLowerCase(Locale.ROOT)
            .replace(" ", "_");
    }

    private static String normalizeTestName(String name) {
        return name.replace(" == ", " equals ").replace("-", " ");
    }
}
