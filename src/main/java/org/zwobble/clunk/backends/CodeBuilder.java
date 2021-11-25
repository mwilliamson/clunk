package org.zwobble.clunk.backends;

public class CodeBuilder {
    private final StringBuilder builder = new StringBuilder();

    public void append(String value) {
        builder.append(value);
    }

    public void newLine() {
        builder.append("\n");
    }

    public String toString() {
        return builder.toString();
    }
}
