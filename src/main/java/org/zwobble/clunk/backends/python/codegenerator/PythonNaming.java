package org.zwobble.clunk.backends.python.codegenerator;

public class PythonNaming {
    public PythonNaming() {
    }

    public static String upperCamelCaseToSnakeCase(String value) {
        var result = new StringBuilder();

        var iterator = value.codePoints().iterator();
        if (!iterator.hasNext()) {
            return "";
        }

        result.appendCodePoint(Character.toLowerCase(iterator.nextInt()));
        while (iterator.hasNext()) {
            var codePoint = iterator.nextInt();
            if (Character.isUpperCase(codePoint)) {
                result.append("_");
                result.appendCodePoint(Character.toLowerCase(codePoint));
            } else {
                result.appendCodePoint(codePoint);
            }
        }

        return result.toString();
    }
}
