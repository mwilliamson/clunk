package org.zwobble.clunk.backends.python.codegenerator;

import java.util.Locale;

public class CaseConverter {
    public static String camelCaseToSnakeCase(String name) {
        var builder = new StringBuilder();

        builder.appendCodePoint(name.codePointAt(0));
        name.codePoints().skip(1).forEachOrdered(codePoint -> {
            if (Character.isUpperCase(codePoint)) {
                builder.append("_");
                builder.appendCodePoint(Character.toLowerCase(codePoint));
            } else {
                builder.appendCodePoint(codePoint);
            }
        });

        return builder.toString();
    }

    public static String lowerCamelCaseToUpperCamelCase(String name) {
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }
}
