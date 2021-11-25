package org.zwobble.clunk.backends;

public class CodeBuilder {
    private final StringBuilder builder = new StringBuilder();
    private int indentation = 0;
    private boolean isNewLine = true;

    public void append(String value) {
        if (isNewLine) {
            builder.append(" ".repeat(indentation * 4));
            isNewLine = false;
        }
        builder.append(value);
    }

    public void newLine() {
        builder.append("\n");
        isNewLine = true;
    }

    public void indent() {
        indentation += 1;
    }

    public void dedent() {
        indentation -= 1;
    }

    public String toString() {
        return builder.toString();
    }
}
