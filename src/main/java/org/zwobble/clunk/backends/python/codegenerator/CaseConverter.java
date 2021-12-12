package org.zwobble.clunk.backends.python.codegenerator;

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
}
