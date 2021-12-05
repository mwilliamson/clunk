package org.zwobble.clunk.sources;

public record FileFragmentSource(
    String filename,
    String contents,
    int characterIndex
) implements Source {
    public static FileFragmentSource create(String filename, String contents) {
        return new FileFragmentSource(filename, contents, 0);
    }

    @Override
    public String describe() {
        var lines = contents.split("\n");
        var position = 0;

        for (var lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            var line = lines[lineIndex];
            var nextLinePosition = position + line.length() + 1;
            if (nextLinePosition > characterIndex || nextLinePosition >= contents.length()) {
                return context(
                    line,
                    lineIndex,
                    characterIndex - position
                );
            }
            position = nextLinePosition;
        }
        throw new RuntimeException("should be impossible (but evidently isn't)");
    }

    @Override
    public Source at(int characterIndex) {
        return new FileFragmentSource(filename, contents, this.characterIndex + characterIndex);
    }

    public Source end() {
        return at(contents.length());
    }

    private String context(String line, int lineIndex, int columnIndex) {
        var lineNumber = lineIndex + 1;
        var columnNumber = columnIndex + 1;
        return filename + ":" + lineNumber + ":" + columnNumber + "\n" + line + "\n" + " ".repeat(columnIndex) + "^";
    }
}
